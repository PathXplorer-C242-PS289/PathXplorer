package com.example.pathxplorer.ui.main

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pathxplorer.databinding.ActivityListCampusOrMajorBinding
import com.example.pathxplorer.ui.main.adapter.GridAdapterCampus
import com.example.pathxplorer.ui.utils.generateListKampus

class ListCampusOrMajorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListCampusOrMajorBinding

    companion object {
        const val TYPE_LIST = "type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()
//        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFFFFF")))
//        supportActionBar?.elevation = 0f
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding = ActivityListCampusOrMajorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = binding.toolbar
        actionBar.setBackgroundColor(Color.parseColor("#FFFFFF"))
        actionBar.elevation = 0f
        actionBar.setNavigationOnClickListener {
            finish()
        }

        val type = intent.getStringExtra(TYPE_LIST)

        val listKampus = generateListKampus()
        binding.rvDetail.layoutManager = GridLayoutManager(this, 2)
        binding.rvDetail.adapter = GridAdapterCampus(listKampus)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}