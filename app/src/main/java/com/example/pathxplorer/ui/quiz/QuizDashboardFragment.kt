package com.example.pathxplorer.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.Result
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.startTestButton.setOnClickListener {
            val intent = Intent(activity, QuizActivity::class.java)
            startActivity(intent)
        }

        lifecycleScope.launch {
            setupAction()
        }

        return root
    }

    private suspend fun setupAction() {
        viewModel.getTestResults().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    isLoading(true)
                }
                is Result.Success -> {
                    isLoading(false)
                    if (result.data.data.testResults.isEmpty()) {
                        binding.rvResults.visibility = View.GONE
                        binding.emptyLayout.visibility = View.VISIBLE
                    } else {
                        binding.rvResults.visibility = View.VISIBLE
                        binding.emptyLayout.visibility = View.GONE

                        val adapter = TestResultAdapter()

                        val linearLayoutManager = LinearLayoutManager(requireContext())
                        binding.rvResults.layoutManager = linearLayoutManager

                        adapter.submitList(result.data.data.testResults)
                        binding.rvResults.adapter = adapter
                    }
                    viewModel.getSession().observe(viewLifecycleOwner) { user ->
                        Log.d("QuizDashboardFragment", "User: ${user.token}")
                    }
                }
                is Result.Error -> {
                    isLoading(false)
                    Log.e("QuizDashboardFragment", "Error: ${result.error}")
                }
            }
        }
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressIndicator.visibility = View.VISIBLE
        } else {
            binding.progressIndicator.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}