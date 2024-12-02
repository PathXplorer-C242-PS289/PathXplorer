package com.example.pathxplorer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultQuiz(
    val indexQ: Int,
    val answer: Int
) : Parcelable

data class Answer(
    val question: String,
    val QuestionNumber: Int,
    val value: Int
)