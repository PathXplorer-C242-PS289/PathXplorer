package com.example.pathxplorer.data.models

data class WebinarResponse(
    val error: Boolean,
    val message: String,
    val listEvents: List<WebinarModel>
)