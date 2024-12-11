package com.example.pathxplorer.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import com.example.pathxplorer.data.local.room.DailyDao
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.data.remote.response.SaveRiasecTestResponse
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository private constructor(
    private val apiService: ApiService,
    private val dailyDao: DailyDao,
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

    suspend fun getRecommendation(code: String): MutableLiveData<Result<RecommendationRiasecResponse>> {
        withContext(Dispatchers.Main) {
            recommendationResult.value = Result.Loading
        }
        try {
            val response = apiService.getRecommendation(code)
            withContext(Dispatchers.Main) {
                recommendationResult.value = Result.Success(response)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                recommendationResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
        return recommendationResult
    }

    private val saveTestResult = MutableLiveData<Result<SaveRiasecTestResponse>>()

    suspend fun saveTest(testId: Int, userId: Int, category: String): MutableLiveData<Result<SaveRiasecTestResponse>> {
        withContext(Dispatchers.Main) {
            saveTestResult.value = Result.Loading
        }
        try {
            val body = ApiService.SaveTestRequest(testId, userId, category)
            val response = apiService.saveResultTest(body)
            withContext(Dispatchers.Main) {
                saveTestResult.value = Result.Success(response)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                saveTestResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
        return saveTestResult
    }

    private val getTestResults = MutableLiveData<Result<ProfileWithTestResponse>>()

    suspend fun getTestResults(): MutableLiveData<Result<ProfileWithTestResponse>> {
        withContext(Dispatchers.Main) {
            getTestResults.value = Result.Loading
        }
        try {
            val response = apiService.getProfile()
            withContext(Dispatchers.Main) {
                getTestResults.value = Result.Success(response)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                getTestResults.value = Result.Error(e.message ?: "An error occurred")
            }
        }
        return getTestResults
    }

    suspend fun findTestResultById(testId: Int): TestResultsItem? {
        return try {
            val response = apiService.getProfile()
            response.data.testResults.find { it.testId == testId }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun insertDailyQuest(dailyQuestEntity: DailyQuestEntity) {
        dailyDao.insertDailyQuest(dailyQuestEntity)
    }

    suspend fun checkDaily(idUser: Int): Int  {
        return dailyDao.checkDailyQuest(idUser)
    }

    suspend fun getDailyQuestById(idUser: Int): DailyQuestEntity {
        return dailyDao.getDailyQuest(idUser)
    }

    suspend fun updateDailyQuestCount(idUser: Int) {
        dailyDao.updateDailyQuestCount(idUser)
    }

    suspend fun updateScore(idUser: Int, score: Int) {
        dailyDao.updateScore(idUser, score)
    }

    companion object {
        fun getInstance(apiService: ApiService, dailyDao: DailyDao, userPreference: UserPreference): UserRepository {
            return UserRepository(apiService, dailyDao, userPreference)
        }
    }
}
