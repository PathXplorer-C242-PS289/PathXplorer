package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.data.models.Kampus
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.FragmentHomeBinding
import com.example.pathxplorer.ui.main.adapter.CarouselAdapter
import com.example.pathxplorer.ui.main.adapter.ListAdapterWebinar
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.ui.utils.generateListKampus

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireContext())
    }

    private val webinarViewModel by viewModels<WebinarViewModel> {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    private lateinit var webinarAdapter: ListAdapterWebinar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserInfo()
        setupRecommendedCampus()
        setupWebinars()
        observeWebinars()
        setupClickListeners()
    }

    private fun setupUserInfo() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            val userNameDisplay = if (user.name.split(" ").size > 2) {
                user.name.split(" ")[0] + " " + user.name.split(" ")[1]
            } else {
                user.name
            }
            binding.tvUserName.text = getString(R.string.welcome_user, userNameDisplay)
        }
    }

    private fun setupRecommendedCampus() {
        val listKampus = generateListKampus()
        binding.rvRecommendedCampus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CarouselAdapter(listKampus) { kampus ->
                val campus = Kampus(
                    kampus.name,
                    kampus.location,
                    kampus.image,
                    listOf("Arsitektur", "Kedokteran", "Statistika")
                )
                val intent = Intent(requireContext(), DetailKampusActivity::class.java).apply {
                    putExtra(DetailKampusActivity.EXTRA_CAMPUS, campus)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupWebinars() {
        binding.rvWebinar.apply {
            layoutManager = LinearLayoutManager(context)
            webinarAdapter = ListAdapterWebinar(ArrayList()) { webinarModel ->
                webinarModel.id?.let { id ->
                    startActivity(DetailWebinarActivity.createIntent(requireContext(), id))
                }
            }
            adapter = webinarAdapter
        }
    }

    private fun observeWebinars() {
        webinarViewModel.webinars.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success<List<WebinarModel>> -> {
                    binding.webinarProgressBar.visibility = View.GONE
                    val limitedWebinars = ArrayList(result.data.take(3))
                    if (limitedWebinars.isEmpty()) {
                        binding.rvWebinar.visibility = View.GONE
                        binding.tvNoWebinars.visibility = View.VISIBLE
                    } else {
                        binding.rvWebinar.visibility = View.VISIBLE
                        binding.tvNoWebinars.visibility = View.GONE
                        webinarAdapter.updateData(limitedWebinars)
                    }
                }
                is Result.Error -> {
                    binding.webinarProgressBar.visibility = View.GONE
                    binding.rvWebinar.visibility = View.GONE
                    binding.tvNoWebinars.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        result.error ?: getString(R.string.error_loading_webinars),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Loading -> {
                    binding.webinarProgressBar.visibility = View.VISIBLE
                    binding.rvWebinar.visibility = View.GONE
                    binding.tvNoWebinars.visibility = View.GONE
                }
                else -> {
                    binding.webinarProgressBar.visibility = View.GONE
                    binding.rvWebinar.visibility = View.GONE
                    binding.tvNoWebinars.visibility = View.VISIBLE
                }
            }
        }
        webinarViewModel.fetchWebinars()
    }

    private fun setupClickListeners() {
        binding.tvSeeAllCampus.setOnClickListener {
            startActivity(Intent(context, ListCampusOrMajorActivity::class.java))
        }

        binding.tvSeeAllWebinar.setOnClickListener {
            // Use Navigation component to navigate
            findNavController().navigate(R.id.action_navigation_home_to_webinarFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}