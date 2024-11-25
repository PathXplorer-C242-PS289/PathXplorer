package com.example.pathxplorer.ui.utils.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pathxplorer.ui.utils.generateListKampus
import com.example.pathxplorer.ui.utils.generateListWebinar

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val campus = generateListKampus()
    val webinar = generateListWebinar()
}