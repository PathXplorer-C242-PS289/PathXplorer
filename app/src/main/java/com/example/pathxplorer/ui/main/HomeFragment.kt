package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.data.models.Kampus
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.FragmentHomeBinding
import com.example.pathxplorer.ui.main.adapter.CarouselAdapter
import com.example.pathxplorer.ui.main.adapter.ListAdapterWebinar
import com.example.pathxplorer.ui.quiz.dailyquest.DailyQuestActivity
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.ui.utils.generateListKampus
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var adapter: PostingTestAdapter

    private val mainViewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireContext())
    }

    private val webinarViewModel by viewModels<WebinarViewModel> {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    companion object {
        const val POST_REF = "posts"
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
        auth = Firebase.auth
        db = Firebase.database
        binding.buttonStartTest.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes") { dialog, which ->
                    Toast.makeText(requireContext(), "Yes", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { dialog, which ->
                    Toast.makeText(requireContext(), "No", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        val manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        binding.rvPosting.layoutManager = manager

        val postingRef = db.getReference(POST_REF)
        val options = FirebaseRecyclerOptions.Builder<TestResultPost>()
            .setQuery(postingRef, TestResultPost::class.java)
            .build()
        adapter = PostingTestAdapter(options)
        binding.rvPosting.adapter = adapter

        setupUserInfo()
        setupRecommendedCampus()
//        setupPostingResultTest()
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

        lifecycleScope.launch {
            mainViewModel.getTestResults().observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Success -> {
                        if (result.data.data.testResults.isEmpty()) {
                            binding.reminderContainer.visibility = View.VISIBLE
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(
                            context,
                            result.error ?: getString(R.string.error_loading_test_results),
                            Toast.LENGTH_SHORT
                        ).show()
                        if (result.error!!.contains("HTTP 401")) {
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Session Expired")
                                .setMessage("Your session has expired. Please login again for security reasons.")
                                .setPositiveButton("OK") { dialog, which ->
                                    logout()
                                }
                                .setCancelable(false)
                                .show()
                        }
                    }
                    is Result.Loading -> {
                    }
                }
            }
        }
    }

    private fun setupPostingResultTest() {
        val manager = LinearLayoutManager(requireActivity())
        manager.stackFromEnd = true
        binding.rvPosting.layoutManager = manager

        val postingRef = db.getReference(POST_REF)
        val options = FirebaseRecyclerOptions.Builder<TestResultPost>()
            .setQuery(postingRef, TestResultPost::class.java)
            .build()
        adapter = PostingTestAdapter(options)
        binding.rvPosting.adapter = adapter
    }

    private fun setupRecommendedCampus() {
        val listKampus = generateListKampus()
//        binding.rvRecommendedCampus.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = CarouselAdapter(listKampus) { kampus ->
//                val campus = Kampus(
//                    kampus.name,
//                    kampus.location,
//                    kampus.image,
//                    listOf("Arsitektur", "Kedokteran", "Statistika")
//                )
//                val intent = Intent(requireContext(), DetailKampusActivity::class.java).apply {
//                    putExtra(DetailKampusActivity.EXTRA_CAMPUS, campus)
//                }
//                startActivity(intent)
//            }
//        }
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
//        binding.tvSeeAllCampus.setOnClickListener {
//            startActivity(Intent(context, ListCampusOrMajorActivity::class.java))
//        }

        binding.tvSeeAllWebinar.setOnClickListener {
            // Use Navigation component to navigate
            findNavController().navigate(R.id.action_navigation_home_to_webinarFragment)
        }

        binding.btnToDailyQuest.setOnClickListener {
            startActivity(Intent(context, DailyQuestActivity::class.java))
        }
    }

    private fun logout() {
        mainViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.provider != "credentials") {
                signOutGoogle()
            } else {
                mainViewModel.logout()
            }
        }
    }

    private fun signOutGoogle() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireActivity())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            mainViewModel.logout()
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}