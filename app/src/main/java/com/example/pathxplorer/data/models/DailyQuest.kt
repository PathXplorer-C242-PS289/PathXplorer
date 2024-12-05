package com.example.pathxplorer.data.models

data class DailyQuizQuestion(
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctAnswer: Int,
    val desciption: String
)
