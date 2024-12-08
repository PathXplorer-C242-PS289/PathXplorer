package com.example.pathxplorer.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.remote.response.LoginWithCredentialResponse
import com.example.pathxplorer.data.remote.response.RegisterWithCredentialResponse
import com.example.pathxplorer.data.remote.response.VerifyOtpResponse
import com.example.pathxplorer.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

class AuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){

    private val registerResult = MutableLiveData<Result<RegisterWithCredentialResponse>>()
    private val loginResult = MutableLiveData<Result<LoginWithCredentialResponse>>()
    private val otpVerifyResult = MutableLiveData<Result<VerifyOtpResponse>>()

    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    suspend fun register(email: String, password: String) : LiveData<Result<RegisterWithCredentialResponse>> {
        registerResult.value = Result.Loading
        try {
            val response = apiService.register(email, password)
            registerResult.value = Result.Success(response)
            _error.value = false
            registerResult.postValue(Result.Success(response))
        } catch (e: HttpException) {
            registerResult.value = Result.Error(e.message)
            _error.value = true
        }

        return registerResult
    }

    suspend fun sendOtp(email: String, otp: String) : LiveData<Result<VerifyOtpResponse>> {
        otpVerifyResult.value = Result.Loading
        try {
            apiService.verifyOtp(email, otp)
            otpVerifyResult.value = Result.Success(VerifyOtpResponse("Success"))
            _error.value = false
        } catch (e: HttpException) {
            val jsonString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonString, VerifyOtpResponse::class.java)
            otpVerifyResult.value = Result.Error(errorBody.message)
            _error.value = true
        }

        return otpVerifyResult
    }

    fun loginUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
            val user = UserModel(
                email = email,
                name = response.user.email,
                token = response.token
            )
            saveSession(user)
        } catch (e: HttpException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun registerUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.register(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.d("AuthRepository", "registerUser: ${e.message}")
            emit(Result.Error(e.message.toString()))
        } catch (e: SocketTimeoutException) {
            emit(Result.Error(e.message.toString()))
        } catch (e: IOException) {
            emit(Result.Error(e.message.toString()))
        }
    }

//    fun sendOtp(email: String, otp: String) = liveData(Dispatchers.IO) {
//        emit(Result.Loading)
//        try {
//            apiService.verifyOtp(email, otp)
//            emit(Result.Success(VerifyOtpResponse("Success")))
//        } catch (e: HttpException) {
//            emit(Result.Error(e.message.toString()))
//        } catch (e: SocketTimeoutException) {
//            emit(Result.Error(e.message.toString()))
//        } catch (e: IOException) {
//            emit(Result.Error(e.message.toString()))
//        }
//    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository = AuthRepository(apiService, userPreference)
    }

}