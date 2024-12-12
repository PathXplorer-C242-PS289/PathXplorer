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
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.FragmentHomeBinding
import com.example.pathxplorer.ui.main.adapter.ListAdapterWebinar
import com.example.pathxplorer.ui.main.adapter.PostingTestAdapter
import com.example.pathxplorer.ui.quiz.dailyquest.DailyQuestActivity
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
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
                .setPositiveButton("Yes") { _, _ ->
                    Toast.makeText(requireContext(), "Yes", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { _, _ ->
                    Toast.makeText(requireContext(), "No", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        val manager = LinearLayoutManager(context)
        manager.reverseLayout = true
        manager.stackFromEnd = true
        binding.rvPosting.layoutManager = manager

        val postingRef = db.getReference(POST_REF)
        val query: Query = postingRef.orderByChild("timestamp")
        val options = FirebaseRecyclerOptions.Builder<TestResultPost>()
            .setQuery(query, TestResultPost::class.java)
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


    private fun setupRecommendedCampus() {
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

//    private fun addQuestion() {
//        val questionDailyRef = db.getReference("question_daily")
//        val questions = listOf(
//            DailyQuestQuestion(
//                question = "Lima sekawan Sano, Joko, Adi, Rimba, dan Ratu selalu semangat berangkat bersama menuju sekolah. Joko selalu menjemput Sano, setelah ia dijemput oleh Adi. Rimba menjadi anak terakhir yang dijemput. Sementara rumah Ratu terletak di antara rumah Joko dan rumah Adi. Berikut ini pernyataan yang BENAR adalah …",
//                option1 = "Rumah Ratu terletak paling jauh",
//                option2 = "Rumah Adi terletak paling jauh",
//                option3 = "Rumah Rimba terletak paling jauh",
//                option4 = "Rumah Sano terletak paling dekat",
//                reference = "Ruang Guru, https://www.ruangguru.com/blog/contoh-soal-tes-skolastik",
//                correctAnswer = 1
//            ),
//            DailyQuestQuestion(
//                question = "Jika bahagia itu sederhana, hal-hal sepel akan mendatangkan kebahagiaan. Jika hal-hal sepele mendatangkan kebahagiaan, orang-orang tidak perlu susah payah untuk mendapatkannya. Banyak orang yang mendapatkan kebahagiaan dengan susah payah. Simpulan yang tepat adalah...",
//                option1 = "Bahagia itu tidak sederhana",
//                option2 = "Bahagia itu sederhana",
//                option3 = "Bahagia itu butuh perjuangan yang tidak sepele",
//                option4 = "Jika hal-hal sepele mendatangkan kebahagiaan, orang-orang selalu bahagia dalam hidupnya.",
//                reference = "Kumparan",
//                correctAnswer = 0
//            )
//        )
//
//        questions.forEach {
//            questionDailyRef.push().setValue(it).addOnCompleteListener {
//                Toast.makeText(requireContext(), "Question added", Toast.LENGTH_SHORT).show()
//            }.addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to add question", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}