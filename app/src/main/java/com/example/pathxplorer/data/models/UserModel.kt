package com.example.pathxplorer.data.models

data class UserModel (
    val email: String,
    val name: String,
    val token: String,
    val provider: String = "credentials",
    val isLogin: Boolean = false
)