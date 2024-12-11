package com.example.pathxplorer.ui.quiz.dailyquest

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import com.example.pathxplorer.data.models.UserModel
import kotlinx.coroutines.launch

class DailyViewModel(private val repository: UserRepository): ViewModel() {

    fun checkDaily(userId: Int): Int {
        var result = 0
        viewModelScope.launch {
            result = repository.checkDaily(userId)
        }
        return result
    }

    fun insertDaily(dailyQuestEntity: DailyQuestEntity) {
        viewModelScope.launch {
            repository.insertDailyQuest(dailyQuestEntity)
        }
    }

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
}