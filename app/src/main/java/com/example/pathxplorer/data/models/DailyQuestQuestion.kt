package com.example.pathxplorer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyQuestQuestion(
    val question: String? = null,
    val option1: String? = null,
    val option2: String? = null,
    val option3: String? = null,
    val option4: String? = null,
    val correctAnswer: Int? = null,
    var value: Int? = null,
    var isChecked: Boolean? = false,
    var isCorrect: Boolean? = false,
    val reference: String? = null
) : Parcelable 