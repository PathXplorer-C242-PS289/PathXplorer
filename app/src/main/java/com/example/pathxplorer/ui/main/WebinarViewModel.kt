package com.example.pathxplorer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.data.models.WebinarModel
import kotlinx.coroutines.launch

class WebinarViewModel(private val repository: WebinarRepository) : ViewModel() {
    private val _webinars = MutableLiveData<Result<List<WebinarModel>>>()
    val webinars: LiveData<Result<List<WebinarModel>>> = _webinars

    fun fetchWebinars() {
        viewModelScope.launch {
            _webinars.value = Result.Loading
            _webinars.value = repository.getWebinars()
        }
    }

    private val _eventDetailLiveData = MutableLiveData<WebinarState>()
    val eventDetailLiveData: LiveData<WebinarState> = _eventDetailLiveData

    fun fetchEventDetail(eventId: Int) {
        viewModelScope.launch {
            _eventDetailLiveData.value = WebinarState.Loading
            when (val result = repository.getWebinarDetail(eventId)) {
                is Result.Success -> _eventDetailLiveData.value = WebinarState.Success(result.data)
                is Result.Error -> _eventDetailLiveData.value = WebinarState.Error(result.error.toString())
                is Result.Loading -> _eventDetailLiveData.value = WebinarState.Loading
            }
        }
    }
}

sealed class WebinarState {
    data object Loading : WebinarState()
    data class Success(val data: WebinarModel) : WebinarState()
    data class Error(val message: String) : WebinarState()
}