package com.example.pathxplorer.di

import android.content.Context
import com.example.pathxplorer.data.local.UserPreference
import com.example.pathxplorer.data.local.dataStore
import com.example.pathxplorer.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}