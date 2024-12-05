package com.example.pathxplorer.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pathxplorer.data.WebinarRepository

class WebinarViewModelFactory(private val repository: WebinarRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WebinarViewModel::class.java)) {
            return WebinarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}