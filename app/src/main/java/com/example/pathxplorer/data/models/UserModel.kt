package com.example.pathxplorer.data.models

data class UserModel (
    val email: String,
    val name: String,
    val token: String, // Token yang digunakan untuk mengakses API
    val provider: String = "credentials",
    val isLogin: Boolean = false,
    val testCount: Int? = 0, // Setiap Test Psikotes diambil akan bertambah 1 kepada user
    val dailyQuestCount: Int? = 0, // Setiap Daily Quest diambil akan bertambah 1 kepada user
    val score: Int? = 0 // Menyimpan setiap score user dari daily quest dijumplahkan lalu dibagi sebanyak dailyQuestCount pada code
)