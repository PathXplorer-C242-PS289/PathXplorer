package com.example.pathxplorer.ui.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.service.Injection
import com.example.pathxplorer.ui.auth.AuthViewModel
import com.example.pathxplorer.ui.main.MainViewModel
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.quiz.dailyquest.DailyViewModel

class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(QuizViewModel::class.java) -> {
                QuizViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DailyViewModel::class.java) -> {
                DailyViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context): UserViewModelFactory {
            val repository = Injection.provideRepository(context)
            return UserViewModelFactory(repository)
        }
    }
}