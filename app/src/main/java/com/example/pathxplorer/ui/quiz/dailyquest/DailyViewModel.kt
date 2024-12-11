package com.example.pathxplorer.ui.quiz.dailyquest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import com.example.pathxplorer.data.models.DailyQuestQuestion
import com.example.pathxplorer.data.models.UserModel
import kotlinx.coroutines.launch
import java.util.ArrayList

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

    private val result = MutableLiveData<DailyQuestEntity>()
    val dailyQuest: LiveData<DailyQuestEntity> = result

    fun getDailyQuestById(idUser: Int) {
        viewModelScope.launch {
            result.value = repository.getDailyQuestById(idUser)
        }
    }

    fun updateDailyQuestCount(idUser: Int) {
        viewModelScope.launch {
            repository.updateDailyQuestCount(idUser)
        }
    }

    fun updateScore(idUser: Int, score: Int) {
        viewModelScope.launch {
            repository.updateScore(idUser, score)
        }
    }

    private val progress = MutableLiveData<Int>()
    val progressQuiz: LiveData<Int> = progress

    fun progressQuizDaily(questions: List<DailyQuestQuestion>) {
        viewModelScope.launch {
            var count = 0
            questions.forEach {
                if (it.isChecked) {
                    count++
                }
            }
            progress.value = count
        }
    }

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
}