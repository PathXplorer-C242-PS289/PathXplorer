package com.example.pathxplorer.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.local.UserModel
import com.example.pathxplorer.data.local.UserPreference
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

//    private val loginResult = MutableLiveData<Result<LoginResponse>>()

//    suspend fun login(email: String, password: String) : LiveData<Result<LoginResponse>> {
//        loginResult.value = Result.Loading
//        try {
//            val client = apiService.login(email, password)
//            loginResult.value = Result.Success(client)
//        } catch (e: HttpException) {
//            val jsonInString = e.response()?.errorBody()?.string()
//            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
//            val errorMessage = errorBody.message
//            loginResult.value = Result.Error(errorMessage)
//        }
//        return loginResult
//    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}