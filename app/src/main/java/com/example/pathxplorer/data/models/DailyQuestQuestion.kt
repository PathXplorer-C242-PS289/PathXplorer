package com.example.pathxplorer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyQuestQuestion(
    val question: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctAnswer: Int,
    var value: Int? = null,
    var isChecked: Boolean = false,
    var isCorrect: Boolean = false
) : Parcelable
