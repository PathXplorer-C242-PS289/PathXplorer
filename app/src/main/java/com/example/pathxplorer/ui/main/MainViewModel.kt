package com.example.pathxplorer.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getTestResults() = liveData {
        emit(Result.Loading)
        try {
            emit(repository.getTestResults().value ?: Result.Error("Failed to load test results"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    private val result = MutableLiveData<DailyQuestEntity>()
    val dailyQuest: LiveData<DailyQuestEntity> = result

    fun getDailyQuestById(idUser: Int) {
        viewModelScope.launch {
            result.value = repository.getDailyQuestById(idUser)
        }
    }

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}