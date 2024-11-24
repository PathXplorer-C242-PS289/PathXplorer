package com.example.pathxplorer.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.databinding.FragmentHomeBinding
import com.example.pathxplorer.ui.details.DetailKampusActivity
import com.example.pathxplorer.ui.listitem.ListCampusOrMajorActivity
import com.example.pathxplorer.ui.utils.adapter.CarouselAdapter
import com.example.pathxplorer.ui.utils.adapter.ListAdapterWebinar
import com.example.pathxplorer.ui.utils.generateListKampus
import com.example.pathxplorer.ui.utils.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        val listKampus = generateListKampus()
        binding.rvRecommendedCampus.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = CarouselAdapter(listKampus) { kampus ->
                val intent = Intent(requireContext(), DetailKampusActivity::class.java).apply {
                    putExtra(DetailKampusActivity.EXTRA_KAMPUS_NAME, kampus.name)
                    putExtra(DetailKampusActivity.EXTRA_KAMPUS_LOCATION, kampus.location)
                    putExtra(DetailKampusActivity.EXTRA_KAMPUS_IMAGE, kampus.image)
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