package com.example.pathxplorer.data.models

data class Question (
    val page: Int,
    val question: String,
    val number: Int,
    var isChecked: Boolean = false,
    var value: Int? = null
)