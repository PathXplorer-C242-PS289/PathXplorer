package com.example.pathxplorer.data.repository

import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.local.UserPreference
import kotlinx.coroutines.flow.Flow

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