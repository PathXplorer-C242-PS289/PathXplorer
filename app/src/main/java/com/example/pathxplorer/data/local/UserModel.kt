package com.example.pathxplorer.data.local

data class UserModel (
    val email: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)