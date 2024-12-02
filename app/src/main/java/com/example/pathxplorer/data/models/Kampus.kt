package com.example.pathxplorer.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kampus(
    val name: String,
    val location: String,
    val image: Int,
    val major: List<String>
) : Parcelable