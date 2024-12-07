package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.databinding.FragmentQuizResultBinding
import com.example.pathxplorer.service.riasec.RiasecHelper
import com.example.pathxplorer.ui.quiz.ResultAdapter

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var riasecHelper: RiasecHelper

    companion object {
        const val RESULT_VALUE = "result_value"
        const val RIASEC_CODE = "riasec_code"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

//        riasecHelper = RiasecHelper(
//            context = requireActivity(),
//            onResult = { resultPredict ->
//                Log.d("RiasecHelper", resultPredict)
//                binding.tvResult.text = resultPredict
//            },
//            onError = { error ->
//                Log.e("RiasecHelper", error)
//            }
//        )

        val resultVal = arguments?.getIntegerArrayList(RESULT_VALUE)
        val riasecCode = arguments?.getString(RIASEC_CODE)

        binding.tvResult.setOnClickListener {
//            riasecHelper.predict(riasecCode!!)
        }

        binding.tvResult.text = riasecCode

        val result = setResultKey(resultVal!!)

        onBackPressedCallback()

        // view
        setupResult(result)
    }

    private fun setupResult(list: MutableMap<Int, Int>) {
        binding.rvResultQuiz.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvResultQuiz.setHasFixedSize(true)
        val adapter = ResultAdapter(list)
        binding.rvResultQuiz.adapter = adapter
    }

    private fun onBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

//    override fun onContextItemSelected(item: MenuItem): Boolean {
//
//        when (item.itemId) {
//            android.R.id.home -> {
//
//            }
//        }
//
//        return super.onContextItemSelected(item)
//
//    }

    private fun setResultKey(result: ArrayList<Int>): MutableMap<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        for (i in 0..<result.size) {
            for (j in 1..5) {
                if (result[i] == j) {
                    if (map[j]==null) {
                        map[j] = 1
                    } else {
                        map[j] = map[j]!! + 1
                    }
                } else {
                    if (map[j]==null) {
                        map[j] = 0
                    }
                }
            }
        }
        Log.d("Result", map.toString())
        return map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}