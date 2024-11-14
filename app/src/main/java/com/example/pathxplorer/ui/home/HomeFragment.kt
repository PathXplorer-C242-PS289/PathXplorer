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
import com.example.pathxplorer.ui.listitem.ListCampusOrMajorActivity
import com.example.pathxplorer.ui.utils.adapter.CarouselAdapter
import com.example.pathxplorer.ui.utils.adapter.ListAdapterWebinar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listKampus = homeViewModel.campus
        binding.rvRecommendedCampus.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val listHeroAdapter = CarouselAdapter(listKampus)
        binding.rvRecommendedCampus.adapter = listHeroAdapter

        val listWebinar = homeViewModel.webinar
        binding.rvWebinar.layoutManager = LinearLayoutManager(context)
        val listWebinarAdapter = ListAdapterWebinar(listWebinar)
        binding.rvWebinar.adapter = listWebinarAdapter

        binding.rvRecommendedCampus.setHasFixedSize(true)

        setAction()

        return root
    }

    private fun setAction() {
        binding.tvSeeAllCampus.setOnClickListener {
            val intent = Intent(context, ListCampusOrMajorActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}