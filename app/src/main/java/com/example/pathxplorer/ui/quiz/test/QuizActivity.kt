package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.R

class QuizActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()
    }
}