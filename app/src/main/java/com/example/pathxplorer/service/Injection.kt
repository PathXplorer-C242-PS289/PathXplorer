package com.example.pathxplorer.service

import android.content.Context
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.data.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}