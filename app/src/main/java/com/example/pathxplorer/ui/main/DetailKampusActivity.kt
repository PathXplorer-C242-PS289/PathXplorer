package com.example.pathxplorer.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.models.Kampus
import com.example.pathxplorer.databinding.ActivityDetailkampusBinding
import com.example.pathxplorer.ui.main.adapter.MajorAdapter

class DetailKampusActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailkampusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailkampusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupData()
        setupRecyclerView()
        setupTabLayout()
        setupClickListeners()
    }

    private fun setupData() {

        val campus = intent.getParcelableExtra<Kampus>(EXTRA_CAMPUS)

        binding.apply {
            tvCampusName.text = campus?.name
            tvCampusLocation.text = campus?.location
            ivCampus.setImageResource(campus?.image ?: 0)
            tvCampusWebsite.text = "https://www.${campus?.name}.ac.id".lowercase().replace(" ", "")
        }
    }

    private fun setupRecyclerView() {
        val majorList = listOf("Arsitektur", "Kedokteran", "Statistika")
        binding.rvMajors.apply {
            layoutManager = LinearLayoutManager(this@DetailKampusActivity)
            adapter = MajorAdapter(majorList)
        }
    }

    private fun setupTabLayout() {
        val totalPages = 8
        for (i in 1..totalPages) {
            binding.tabLayout.addTab(binding.tabLayout.newTab().setText(i.toString()))
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_KAMPUS_NAME = "extra_kampus_name"
        const val EXTRA_KAMPUS_LOCATION = "extra_kampus_location"
        const val EXTRA_KAMPUS_IMAGE = "extra_kampus_image"
        const val EXTRA_CAMPUS = "extra_campus"
    }
}

