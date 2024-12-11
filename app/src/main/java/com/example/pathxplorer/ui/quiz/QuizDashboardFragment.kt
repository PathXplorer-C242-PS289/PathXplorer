package com.example.pathxplorer.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.databinding.FragmentQuizDashboardBinding
import com.example.pathxplorer.ui.quiz.test.QuizActivity
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class QuizDashboardFragment : Fragment() {

    private var _binding: FragmentQuizDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<QuizViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

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

        auth = Firebase.auth

        setupInitialState()
        setupActionButtons()
        loadTestResults()
    }

    private fun setupInitialState() {
        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = TestResultAdapter()
        }
        showLoading(true)
        showEmptyState(false)
    }

    private fun setupActionButtons() {
        binding.startTestButton.setOnClickListener {
            startActivity(Intent(requireContext(), QuizActivity::class.java))
        }
    }

    private fun loadTestResults() {
        // Use viewLifecycleOwner for observing LiveData
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.getTestResults().observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            showEmptyState(false)
                        }
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
            } catch (e: Exception) {
                showLoading(false)
                showError(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun handleTestResults(data: ProfileWithTestResponse) {
        if (data.data.testResults.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            (binding.rvResults.adapter as? TestResultAdapter)?.submitList(data.data.testResults)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) {
            binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (_binding != null) {
            binding.rvResults.visibility = if (isEmpty) View.GONE else View.VISIBLE
            binding.emptyLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun showError(message: String) {
        if (isAdded && context != null) {
            Log.e("QuizDashboardFragment", message)
            val invalidToken = "HTTP 401"
            if (message.contains(invalidToken)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Session Expired")
                    .setMessage("Your session has expired. Please login again for security reasons.")
                    .setPositiveButton("OK") { _, _ ->
                        logout()
                    }
                    .setCancelable(false)
                    .show()
                return
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.provider != "credentials") {
                signOutGoogle()
            } else {
                viewModel.logout()
            }
        }
    }

    private fun signOutGoogle() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireActivity())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            viewModel.logout()
        }
    }

    override fun onResume() {
        super.onResume()
        loadTestResults()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}