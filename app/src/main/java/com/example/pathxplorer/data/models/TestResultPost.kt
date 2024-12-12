package com.example.pathxplorer.data.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@IgnoreExtraProperties
@Parcelize
data class TestResultPost (
    val userId: Int? = null,
    val id: Int? = null,
    val nameOwner: String? = null,
    val riasecType: String? = null,
    val title: String? = null,
    val body: String? = null,
    val timestamp: String? = null
): Parcelable