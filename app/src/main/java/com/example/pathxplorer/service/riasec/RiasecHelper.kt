package com.example.pathxplorer.service.riasec

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import org.tensorflow.lite.InterpreterApi
import org.tensorflow.lite.gpu.GpuDelegateFactory
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class RiasecHelper(
    private val modelName: String = "model.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
) {

    private var isGPUSupported: Boolean = false
    private var interpreter: InterpreterApi? = null

    init {
        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
            val optionsBuilder = TfLiteInitializationOptions.builder()
            if (gpuAvailable) {
                optionsBuilder.setEnableGpuDelegateSupport(true)
                isGPUSupported = true
            }
            TfLite.initialize(context, optionsBuilder.build())
        }.addOnSuccessListener {
            loadLocalModel()
        }.addOnFailureListener {
            onError("Failed to initialize TFLite: ${it.message}")
        }
    }

    private fun loadLocalModel() {
        try {
            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
            initializeInterpreter(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    fun initializeInterpreter(model: Any) {
        interpreter?.close()
        try {
            val options = InterpreterApi.Options()
                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
            if (isGPUSupported){
                options.addDelegateFactory(GpuDelegateFactory())
            }
            if (model is ByteBuffer) {
                interpreter = InterpreterApi.create(model, options)
            }
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    fun predict(inputString: String) {
        if (interpreter == null) {
            return
        }

        val inputArray = Array(1) { FloatArray(48) }
        val input = inputString.split(",").map { it.toFloat() }

        for (i in input.indices) {
            inputArray[0][i] = input[i]
        }

        val outputArray = Array(1) { FloatArray(6) }

        try {
            interpreter?.run(inputArray, outputArray)

//            onResult(outputArray)

            // Log input details
            val inputTensor = interpreter?.getInputTensor(0)
            val inputShape = inputTensor?.shape()
            val inputDataType = inputTensor?.dataType()
            Log.d(TAG, "Input Tensor - Shape: ${inputShape?.contentToString()}, Data Type: $inputDataType")

            // Log output details
            val outputTensor = interpreter?.getOutputTensor(0)
            val outputShape = outputTensor?.shape()
            val outputDataType = outputTensor?.dataType()
            Log.d(TAG, "Output Tensor - Shape: ${outputShape?.contentToString()}, Data Type: $outputDataType")
            Log.d(TAG, "Prediction result: $outputArray")

            val result = FloatArray(6)
            for (i in outputArray[0].indices) {
                result[i] = outputArray[0][i]
            }
            val binaryArray = convertToBinaryArray(result)
            val resultString = convertToCode(binaryArray)
//            val resultString = binaryArray.joinToString(separator = ",")
            onResult(resultString)
            Log.d(TAG, "Prediction result: $resultString")
//            onResult(result.joinToString(separator = ","))
//            Log.d(TAG, "Prediction result: ${result.joinToString(separator = ",")}")
        } catch (e: Exception) {
            onError(e.message.toString())
            Log.e(TAG, e.message.toString())
        }
    }

    private fun convertToBinaryArray(input: FloatArray, threshold: Float = 0.2f): IntArray {
        return input.map { if (it >= threshold) 1 else 0 }.toIntArray()
    }

    private fun convertToCode(input: IntArray): String {
        val riasecCode = listOf("R", "I", "A", "S", "E", "C")
        return input.mapIndexed { index, value -> if (value == 1) riasecCode[index] else "" }
            .filter { it.isNotEmpty() }
            .joinToString(separator = ",")
    }

    companion object {
        private const val TAG = "RiasecHelper"
    }
}