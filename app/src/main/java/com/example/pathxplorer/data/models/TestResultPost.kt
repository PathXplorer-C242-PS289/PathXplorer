package com.example.pathxplorer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TestResultPost (
    val userId: Int? = null,
    val id: Int? = null,
    val riasecType: String = "",
    val title: String = "",
    val body: String = ""
): Parcelable