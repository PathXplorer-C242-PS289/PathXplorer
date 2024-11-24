package com.example.pathxplorer.ui.details

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.databinding.ActivityDetailkampusBinding
import com.example.pathxplorer.ui.utils.adapter.MajorAdapter

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
        val name = intent.getStringExtra(EXTRA_KAMPUS_NAME)
        val location = intent.getStringExtra(EXTRA_KAMPUS_LOCATION)
        val image = intent.getIntExtra(EXTRA_KAMPUS_IMAGE, 0)

        binding.apply {
            tvCampusName.text = name
            tvCampusLocation.text = location
            ivCampus.setImageResource(image)
            tvCampusWebsite.text = "https://www.$name.ac.id".lowercase().replace(" ", "")
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
    }
}

