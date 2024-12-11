package com.example.pathxplorer.ui.quiz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.databinding.ActivityDetailTestResultBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory

class DetailTestResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TEST_ID = "extra_test_id"
    }

    private lateinit var binding: ActivityDetailTestResultBinding
    private val viewModel by viewModels<QuizViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        loadTestResult()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Test Result Detail"
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadTestResult() {
        val testId = intent.getIntExtra(EXTRA_TEST_ID, -1)
        if (testId != -1) {
            viewModel.getSession().observe(this) { _ ->
                viewModel.getTestDetailById(testId).observe(this) { testItem: TestResultsItem? ->
                    if (testItem != null) {
                        showTestResultDetail(testItem)
                    } else {
                        showErrorState()
                    }
                }
            }
        } else {
            showErrorState()
        }
    }

    private fun showTestResultDetail(testItem: TestResultsItem) {
        val imageResource = when(testItem.riasecType?.uppercase()) {
            "R" -> R.drawable.r_type
            "I" -> R.drawable.i_type
            "A" -> R.drawable.a_type
            "S" -> R.drawable.s_type
            "E" -> R.drawable.e_type
            "C" -> R.drawable.c_type
            else -> R.drawable.r_type
        }
        binding.ivRiasecType.setImageResource(imageResource)

        val riasecFullName = when(testItem.riasecType?.uppercase()) {
            "R" -> "Realistic"
            "I" -> "Investigative"
            "A" -> "Artistic"
            "S" -> "Social"
            "E" -> "Enterprising"
            "C" -> "Conventional"
            else -> "Unknown Type"
        }

        with(binding) {
            tvRiasecType.text = riasecFullName
            tvInterestDescription.text = testItem.interestDescription ?: "-"
            tvKeySkills.text = testItem.keySkills ?: "-"
            tvExampleCareers.text = testItem.exampleCareers ?: "-"
            tvTestDate.text = testItem.timestamp ?: "-"
            tvScore.text = "10% Accuracy"
        }
    }

    private fun showErrorState() {
        with(binding) {
            ivRiasecType.setImageResource(R.drawable.r_type)
            tvRiasecType.text = getString(R.string.no_data)
            tvInterestDescription.text = getString(R.string.no_data)
            tvKeySkills.text = getString(R.string.no_data)
            tvExampleCareers.text = getString(R.string.no_data)
            tvTestDate.text = getString(R.string.no_data)
            tvScore.text = "-"
        }
    }
}