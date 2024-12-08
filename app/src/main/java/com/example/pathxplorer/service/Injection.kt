package com.example.pathxplorer.service

import android.content.Context
import com.example.pathxplorer.data.AuthRepository
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.remote.retrofit.ApiConfig
import com.example.pathxplorer.data.remote.retrofit.ApiService

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        return AuthRepository.getInstance(apiService, pref)
    }
}