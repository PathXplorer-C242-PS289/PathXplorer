package com.example.pathxplorer.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.models.UserModel
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

    fun registerUser(userName: String, email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = ApiService.RegisterRequest(userName, email, password)
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

    fun registerWithGoogle(idToken: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = ApiService.AuthWithGoogleRequest(idToken)
            val response = apiService.registerWithGoogle(request)
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

    fun loginUser(email: String, password: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.LoginRequest(email, password)
            val response = apiService.login(body)
            val user = UserModel(
                email = response.user.email,
                name = response.user.username,
                token = response.token,
                userId = response.user.id
            )
            Log.d(TAG, "loginUser: $response")
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

    fun loginWithGoogle(idToken: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val request = ApiService.AuthWithGoogleRequest(idToken)
            val response = apiService.loginWithGoogle(request)
            _error.postValue(false)
            emit(Result.Success(response))
            Log.d(TAG, "loginWithGoogle: $response")
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

    fun resendOtp(email: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.SendNewOtpRequest(email)
            apiService.sendNewOtp(body)
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

    fun forgotPassword(email: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.forgotPasswordRequest(email)
            val response = apiService.ForgotPassword(body)
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

    fun verifyOtpForgotPassword(email: String, otp: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.VerifyRequest(email, otp)
            val response = apiService.verifyOtpForgotPassword(body)
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

    fun resetPassword(email: String, otp: String, newPassword: String) = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val body = ApiService.ResetPasswordRequest(email, otp, newPassword)
            val response = apiService.resetPassword(body)
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

    companion object {
        private const val TAG = "AuthRepository"

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): AuthRepository = AuthRepository(apiService, userPreference)
    }
}