package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.databinding.ActivityDetailTestResultBinding
import com.example.pathxplorer.ui.quiz.QuizViewModel
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

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun showTestResultDetail(testItem: TestResultsItem) {
        binding.tvRiasecType.text = testItem.riasecType ?: "-"

        binding.tvInterestDescription.text = testItem.interestDescription ?: "-"
        binding.tvKeySkills.text = testItem.keySkills ?: "-"
        binding.tvExampleCareers.text = testItem.exampleCareers ?: "-"
    }

    private fun showErrorState() {
        binding.tvRiasecType.text = "No Data"
        binding.tvInterestDescription.text = "No Data"
        binding.tvKeySkills.text = "No Data"
        binding.tvExampleCareers.text = "No Data"
    }
}
