package com.example.pathxplorer.data

import androidx.lifecycle.MutableLiveData
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.response.SaveRiasecTestResponse
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

    private val recommendationResult = MutableLiveData<Result<RecommendationRiasecResponse>>()

    suspend fun getRecommendation(code: String) : MutableLiveData<Result<RecommendationRiasecResponse>> {
        recommendationResult.value = Result.Loading
        try {
            val response = apiService.getRecommendation(code)
            recommendationResult.value = Result.Success(response)
        } catch (e: Exception) {
            recommendationResult.value = Result.Error(e.message ?: "An error occurred")
        }
        return recommendationResult
    }

    private val saveTestResult = MutableLiveData<Result<SaveRiasecTestResponse>>()

    suspend fun saveTest(testId: Int, userId: Int, category: String): MutableLiveData<Result<SaveRiasecTestResponse>> {
        saveTestResult.value = Result.Loading
        try {
            val body = ApiService.SaveTestRequest(testId, userId, category)
            val response = apiService.saveResultTest(body)
            saveTestResult.value = Result.Success(response)
        } catch (e: Exception) {
            saveTestResult.value = Result.Error(e.message ?: "An error occurred")
        }
        return saveTestResult
    }

    private val getTestResults = MutableLiveData<Result<ProfileWithTestResponse>>()

    suspend fun getTestResults(): MutableLiveData<Result<ProfileWithTestResponse>> {
        getTestResults.value = Result.Loading
        try {
            val response = apiService.getProfile()
            getTestResults.value = Result.Success(response)
        } catch (e: Exception) {
            getTestResults.value = Result.Error(e.message ?: "An error occurred")
        }
        return getTestResults
    }

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