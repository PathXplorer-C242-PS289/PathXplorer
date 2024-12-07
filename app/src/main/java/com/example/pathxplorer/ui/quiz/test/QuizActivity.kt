package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.pathxplorer.R
import com.example.pathxplorer.service.riasec.RiasecHelper
import com.example.pathxplorer.ui.main.MainViewModel
import com.example.pathxplorer.ui.quiz.QuizDashboardFragment
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.utils.CustomDialog

class QuizActivity : AppCompatActivity() {

    private lateinit var riasecHelper: RiasecHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        riasecHelper = RiasecHelper(
            context = this,
            onResult = { resultPredict ->
                Log.d("QuizActivity", "Result: $resultPredict")
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )

        riasecHelper.initializeInterpreter() {
            Toast.makeText(this, "Model loaded", Toast.LENGTH_SHORT).show()
            Log.d("QuizActivity", "Model loaded")
        }
//        enableEdgeToEdge()
        supportActionBar?.hide()
    }
}