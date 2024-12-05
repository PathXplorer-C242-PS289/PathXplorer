package com.example.pathxplorer.data.models

data class WebinarDetailResponse(
    val error: Boolean,
    val message: String,
    val event: WebinarModel
)