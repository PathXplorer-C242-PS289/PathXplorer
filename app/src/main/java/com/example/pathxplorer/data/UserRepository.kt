package com.example.pathxplorer.data

import androidx.lifecycle.MutableLiveData
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val apiService: ApiService,
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

    private val recommecdationResult = MutableLiveData<Result<RecommendationRiasecResponse>>()

    suspend fun getRecommendation(code: String) : MutableLiveData<Result<RecommendationRiasecResponse>> {
        recommecdationResult.value = Result.Loading
        try {
            val response = apiService.getRecommendation(code)
            recommecdationResult.value = Result.Success(response)
        } catch (e: Exception) {
            recommecdationResult.value = Result.Error(e.message ?: "An error occurred")
        }
        return recommecdationResult
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
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}