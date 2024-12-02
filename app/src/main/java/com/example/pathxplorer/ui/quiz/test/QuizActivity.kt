package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.main.MainViewModel
import com.example.pathxplorer.ui.quiz.QuizDashboardFragment
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.utils.CustomDialog

class QuizActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.hide()
    }
}