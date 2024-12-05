package com.example.pathxplorer.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.databinding.FragmentWebinarBinding
import com.example.pathxplorer.ui.main.adapter.WebinarAdapter

class WebinarFragment : Fragment() {
    private var _binding: FragmentWebinarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WebinarViewModel by viewModels {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    private lateinit var webinarAdapter: WebinarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebinarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeWebinars()
        viewModel.fetchWebinars()
    }

    private fun setupRecyclerView() {
        webinarAdapter = WebinarAdapter { webinar ->
            webinar.id?.let { id ->
                startActivity(DetailWebinarActivity.createIntent(requireContext(), id))
            }
        }

        binding.rvWebinars.apply {
            adapter = webinarAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun observeWebinars() {
        viewModel.webinars.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    webinarAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error.toString())
                }
                is Result.Loading -> showLoading(true)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}