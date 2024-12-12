package com.example.pathxplorer.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.databinding.FragmentQuizDashboardBinding
import com.example.pathxplorer.ui.quiz.test.QuizActivity
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import kotlinx.coroutines.launch

class QuizDashboardFragment : Fragment() {

    private var _binding: FragmentQuizDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<QuizViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }
    private lateinit var testResultAdapter: TestResultAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupActionButtons()
        loadTestResults()
        setupAnimations()
    }

    private fun setupRecyclerView() {
        testResultAdapter = TestResultAdapter { selectedItem: TestResultsItem ->
            showDetail(selectedItem)
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = testResultAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupActionButtons() {
        binding.startTestButton.setOnClickListener {
            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }
    }

    private fun loadTestResults() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getTestResults().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        handleTestResults(result.data)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showError(result.error ?: "Unknown error occurred")
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleTestResults(data: ProfileWithTestResponse) {
        val testResults = data.data.testResults
        if (testResults.isEmpty()) {
            binding.rvResults.visibility = View.GONE
            binding.emptyLayout.visibility = View.VISIBLE
        } else {
            binding.rvResults.visibility = View.VISIBLE
            binding.emptyLayout.visibility = View.GONE
            testResultAdapter.submitList(testResults)
        }
    }

    private fun showError(message: String) {
        if (isAdded && context != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDetail(item: TestResultsItem) {
        val intent = Intent(requireContext(), DetailTestResultActivity::class.java)
        intent.putExtra(DetailTestResultActivity.EXTRA_TEST_ID, item.testId)
        startActivity(intent)
    }

    private fun setupAnimations() {
        binding.welcomeText.alpha = 0f
        binding.startTestButton.alpha = 0f
        binding.welcomeText.animate().alpha(1f).setDuration(500).start()
        binding.startTestButton.animate().alpha(1f).translationYBy(-50f).setDuration(500).start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
