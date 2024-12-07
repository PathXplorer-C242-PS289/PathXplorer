package com.example.pathxplorer.ui.utils

import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.DailyQuizQuestion
import com.example.pathxplorer.data.models.Question

// this is nessasary before API from CC is Complete

data class Kampus(
    val name: String,
    val location: String,
    val image: Int
)

fun generateListKampus(): ArrayList<Kampus> {
    val listKampus = ArrayList<Kampus>()

    for (i in 1..20) {
        listKampus.add(
            Kampus(
                "Kampus $i",
                "Location $i",
                R.drawable.univ
            )
        )
    }

    return listKampus
}

data class Major (
    val name: String,
    val faculy: String,
    val image: Int
)

fun generateListMajor(): ArrayList<Major> {
    val listMajor = ArrayList<Major>()

    for (i in 1..20) {
        listMajor.add(
            Major(
                "Major $i",
                "Faculty $i",
                R.drawable.ic_launcher_foreground
            )
        )
    }

    return listMajor
}

data class Webinar (
    val name: String,
    val date: String,
    val image: Int
)

fun generateListWebinar(): ArrayList<Webinar> {
    val listWebinar = ArrayList<Webinar>()

    for (i in 1..5) {
        listWebinar.add(
            Webinar(
                "Webinar $i",
                "$i Januari 2024",
                R.drawable.webinar
            )
        )
    }

    return listWebinar
}

val filterKampus = listOf(
    "All",
    "Private",
    "State",
    "Religious",
    "Foreign"
)

fun generateDummyQuestionV2(): ArrayList<MutableList<Question>> {
    val dummyQuestion = ArrayList<MutableList<Question>>()
    if (dummyQuestion.isNotEmpty()) {
        return dummyQuestion
    }
    var numberQ = 1
    for (i in 1..6) {
        val questionList = mutableListOf<Question>()
        for (j in 1..8) {
            questionList.add(Question(i, "Question $numberQ", numberQ))
            numberQ++
        }
        dummyQuestion.add(questionList)
    }
    return dummyQuestion
}

fun generateDummyDailyQuizQuestion(): ArrayList<DailyQuizQuestion> {
    val dummyDailyQuizQuestion = ArrayList<DailyQuizQuestion>()
    if (dummyDailyQuizQuestion.isNotEmpty()) {
        return dummyDailyQuizQuestion
    }
    for (i in 1..5) {
        dummyDailyQuizQuestion.add(
            DailyQuizQuestion(
                "Question $i",
                "Option 1",
                "Option 2",
                "Option 3",
                "Option 4",
                1,
                "Description $i"
            )
        )
    }
    return dummyDailyQuizQuestion
}