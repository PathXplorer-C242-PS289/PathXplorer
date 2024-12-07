package com.example.pathxplorer.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.AuthRepository
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository): ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
    fun register(email: String, password: String) = repository.registerUser(email, password)

    fun login(email: String, password: String) = repository.loginUser(email, password)

    fun verifyOtp(email: String, otp: String) = repository.sendOtp(email, otp)

    fun logout() {
        auth.signOut()
    }

//    fun sendOtp(email: String, otp: String) = repository.sendOtp(email, otp)
}