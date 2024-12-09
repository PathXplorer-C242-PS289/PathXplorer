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
    fun register(userName: String, email: String, password: String) = repository.registerUser(userName, email, password)

    fun registerWithGoogle(idToken: String) = repository.registerWithGoogle(idToken)

    fun login(email: String, password: String) = repository.loginUser(email, password)

    fun loginWithGoogle(idToken: String) = repository.loginWithGoogle(idToken)

    fun verifyOtp(email: String, otp: String) = repository.sendOtp(email, otp)

    fun resendOtp(email: String) = repository.resendOtp(email)

    fun forgotPassword(email: String) = repository.forgotPassword(email)

    fun verifyOtpForgotPassword(email: String, otp: String) = repository.verifyOtpForgotPassword(email, otp)

    fun resetPassword(email: String, otp: String, password: String) = repository.resetPassword(email, otp, password)

    fun logout() {
        auth.signOut()
    }

//    fun sendOtp(email: String, otp: String) = repository.sendOtp(email, otp)
}