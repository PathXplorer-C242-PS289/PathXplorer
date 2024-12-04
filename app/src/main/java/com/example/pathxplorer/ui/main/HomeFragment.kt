package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.models.Kampus
import com.example.pathxplorer.databinding.FragmentHomeBinding
import com.example.pathxplorer.ui.main.adapter.CarouselAdapter
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.ui.utils.generateListKampus

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireContext())
    }

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

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val userNameDisplay = if (user.name.split(" ").size > 2) {
                user.name.split(" ")[0] + " " + user.name.split(" ")[1]
            } else {
                user.name
            }
            binding.tvUserName.text = "Welcome, ${userNameDisplay}"
        }

        val listKampus = generateListKampus()
        binding.rvRecommendedCampus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CarouselAdapter(listKampus) { kampus ->

                val campups = Kampus(
                    kampus.name,
                    kampus.location,
                    kampus.image,
                    listOf("Arsitektur", "Kedokteran", "Statistika")
                )

                val intent = Intent(requireContext(), DetailKampusActivity::class.java).apply {
                    putExtra(DetailKampusActivity.EXTRA_CAMPUS, campups)
                }
                startActivity(intent)
            }
        }

        binding.tvSeeAllCampus.setOnClickListener {
            startActivity(Intent(context, ListCampusOrMajorActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}