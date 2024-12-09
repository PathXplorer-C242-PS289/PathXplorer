package com.example.pathxplorer.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.models.Answer
import com.example.pathxplorer.data.models.Question
import com.example.pathxplorer.data.models.ResultQuiz
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.ui.utils.getQuestion

class QuizViewModel(private val repository: UserRepository) : ViewModel() {
    private var _indexedValue = MutableLiveData<Int>().apply { value = 0 }
    val indexedValue: LiveData<Int> = _indexedValue

    private var _isLastQuestion = MutableLiveData<Boolean>().apply { value = false }

    fun isEndOfQuestion(listQuestions: ArrayList<MutableList<Question>>): Boolean {
        if (_indexedValue.value == listQuestions.size - 1) {
            _isLastQuestion.value = true
        }
        return _isLastQuestion.value ?: false
    }

    fun addIndexedValue() {
        _indexedValue.value = _indexedValue.value?.plus(1)
        answerSize = 0
    }

    val questions = arrayListOf<MutableList<Question>>()

    init {
        if (questions.isEmpty()) {
            questions.addAll(getQuestion())
        }
    }

    private var answers: MutableList<MutableList<Answer>> = arrayListOf()
    var answerSize = 0

    fun addAnswer(answer: Answer) {

        if (answers.isEmpty()) {
            for (i in 0 until questions.size) {
                answers.add(arrayListOf())
            }
        }

        questions[_indexedValue.value!!].find {
            it.question == answer.question
        }?.let {
            if (it.isChecked) {
                return
            } else {
                it.isChecked = true
                it.value = answer.value

                answers[_indexedValue.value!!].add(answer)
                answerSize++
            }
        }
    }

    private fun sortAnswer(): MutableList<MutableList<Answer>> {
        val sortedAnswer = mutableListOf<MutableList<Answer>>()
        for (i in 0 until questions.size) {
            sortedAnswer.add(answers[i].sortedBy { it.QuestionNumber }.toMutableList())
        }

        return sortedAnswer
    }

    fun resultAnswer(): ArrayList<Int> {
        val sortedAnswer = sortAnswer()

        val resultQuizMap = arrayListOf<ResultQuiz>()
        val resultValue = arrayListOf<Int>()
        for (i in 0 until sortedAnswer.size) {
            for (j in 0 until sortedAnswer[i].size) {
                val res = ResultQuiz(
                    sortedAnswer[i][j].QuestionNumber,
                    sortedAnswer[i][j].value
                )
                resultValue.add(sortedAnswer[i][j].value)
                resultQuizMap.add(res)
            }
        }

        return resultValue
    }

    fun progress(position: Int): Int {
        return if (position % questions[_indexedValue.value!!].size != 0) {
            answerSize % questions[_indexedValue.value!!].size
        } else {
            questions[_indexedValue.value!!].size
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun getRecommendation(code: String) = repository.getRecommendation(code)

    suspend fun saveTest(testId: Int, userId: Int, category: String) = repository.saveTest(testId, userId, category)

    suspend fun getTestResults() = repository.getTestResults()
}