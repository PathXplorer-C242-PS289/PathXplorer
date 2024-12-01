package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.main.MainViewModel
import com.example.pathxplorer.ui.quiz.QuizDashboardFragment
import com.example.pathxplorer.ui.quiz.QuizViewModel

class QuizActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()

    companion object {
        const val NAVIGATE = "navigate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val navigateTo = intent.getStringExtra(NAVIGATE)
        if (navigateTo == "QuizDashboardFragment") {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, QuizDashboardFragment())
                .commit()
        }

        supportActionBar?.hide()
    }

    fun stopActivity() {
        finish()
    }
}