package com.example.pathxplorer.service

import android.content.Context
import com.example.pathxplorer.data.AuthRepository
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.room.DailyDatabase
import com.example.pathxplorer.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val dailyDao = DailyDatabase.getInstance(context).dailyDao()
        return UserRepository.getInstance(apiService, dailyDao, pref)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AuthRepository.getInstance(apiService, pref)
    }
}