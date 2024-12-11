package com.example.pathxplorer.ui.quiz

import androidx.lifecycle.*
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.models.Answer
import com.example.pathxplorer.data.models.Question
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.ui.utils.getQuestion
import com.example.pathxplorer.data.models.*
import com.example.pathxplorer.ui.utils.getQuestion
import kotlinx.coroutines.launch
import retrofit2.HttpException
class QuizViewModel(private val repository: UserRepository) : ViewModel() {
    private var _indexedValue = MutableLiveData<Int>().apply { value = 0 }
    val indexedValue: LiveData<Int> = _indexedValue

    private var _isLastQuestion = MutableLiveData<Boolean>().apply { value = false }
    val isLastQuestion: LiveData<Boolean> = _isLastQuestion

    val questions = arrayListOf<MutableList<Question>>().apply {
        addAll(getQuestion())
    }

    private var answers: MutableList<MutableList<Answer>> = arrayListOf()
    var answerSize = 0

    fun isEndOfQuestion(listQuestions: ArrayList<MutableList<Question>>): Boolean {
        val isLast = _indexedValue.value == listQuestions.size - 1
        _isLastQuestion.value = isLast
        return isLast
    }

    fun addIndexedValue() {
        _indexedValue.value = _indexedValue.value?.plus(1)
        answerSize = 0
    }

    fun addAnswer(answer: Answer) {
        if (answers.isEmpty()) {
            for (i in 0 until questions.size) {
                answers.add(arrayListOf())
            }
        }

        val currentIndex = _indexedValue.value ?: return
        questions[currentIndex].find { it.question == answer.question }?.let {
            if (!it.isChecked) {
                it.isChecked = true
                it.value = answer.value
                answers[currentIndex].add(answer)
                answerSize++
            }
        }
    }

    fun resultAnswer(): ArrayList<Int> {
        val sortedAnswer = sortAnswer()
        return ArrayList(sortedAnswer.flatten().map { it.value })
    }

    private fun sortAnswer(): MutableList<MutableList<Answer>> {
        return MutableList(questions.size) { i ->
            answers.getOrNull(i)?.sortedBy { it.QuestionNumber }?.toMutableList() ?: mutableListOf()
        }
    }

    fun progress(position: Int): Int {
        val currentIndex = _indexedValue.value ?: return 0
        val questionSize = questions[currentIndex].size
        return if (position % questionSize != 0) {
            answerSize % questionSize
        } else {
            questionSize
        }
    }

    fun getSession(): LiveData<UserModel> = repository.getSession().asLiveData()
    
    fun getRecommendation(code: String) = liveData {
        emit(Result.Loading)
        try {
            emit(repository.getRecommendation(code).value ?: Result.Error("Failed to get recommendation"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun saveTest(testId: Int, userId: Int, category: String) = liveData {
        emit(Result.Loading)
        try {
            emit(repository.saveTest(testId, userId, category).value ?: Result.Error("Failed to save test"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
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
    
    fun getTestDetailById(testId: Int) = liveData {
        emit(null)
        try {
            val detail = repository.findTestResultById(testId)
            emit(detail)
        } catch (e: Exception) {
            emit(null)
        }
    }

}
