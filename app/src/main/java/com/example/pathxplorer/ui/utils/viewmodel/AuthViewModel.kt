package com.example.pathxplorer.ui.utils.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.local.UserModel
import com.example.pathxplorer.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UserRepository): ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}