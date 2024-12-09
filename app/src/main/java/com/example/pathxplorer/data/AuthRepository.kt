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
) {
    private var _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun registerUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = ApiService.RegisterRequest(email, password)
            val response = apiService.register(request)
            emit(Result.Success(response))
        } catch (e: Exception) {
            _error.postValue(true)
            when (e) {
                is HttpException -> {
                    Log.d(TAG, "registerUser: ${e.message}")
                    emit(Result.Error(e.message()))
                }
                is SocketTimeoutException -> emit(Result.Error("Connection timed out"))
                is IOException -> emit(Result.Error("Network error occurred"))
                else -> emit(Result.Error("An unexpected error occurred"))
            }
        }
    }

    fun loginUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.LoginRequest(email, password)
            val response = apiService.login(body)
            val user = UserModel(
                email = email,
                name = response.user.email,
                token = response.token,
                userId = response.user.id
            )
            saveSession(user)
            _error.postValue(false)
            emit(Result.Success(response))
        } catch (e: Exception) {
            _error.postValue(true)
            when (e) {
                is HttpException -> emit(Result.Error(e.message()))
                is SocketTimeoutException -> emit(Result.Error("Connection timed out"))
                is IOException -> emit(Result.Error("Network error occurred"))
                else -> emit(Result.Error("An unexpected error occurred"))
            }
        }
    }

    fun sendOtp(email: String, otp: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.VerifyRequest(email, otp)
            apiService.verifyOtp(body)
            _error.postValue(false)
            emit(Result.Success(VerifyOtpResponse("Success")))
        } catch (e: Exception) {
            _error.postValue(true)
            when (e) {
                is HttpException -> {
                    val errorBody = try {
                        val jsonString = e.response()?.errorBody()?.string()
                        Gson().fromJson(jsonString, VerifyOtpResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    emit(Result.Error(errorBody?.message ?: "Verification failed"))
                }
                is SocketTimeoutException -> emit(Result.Error("Connection timed out"))
                is IOException -> emit(Result.Error("Network error occurred"))
                else -> emit(Result.Error("An unexpected error occurred"))
            }
        }
    }

    companion object {
        private const val TAG = "AuthRepository"

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository = AuthRepository(apiService, userPreference)
    }
}