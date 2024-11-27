package com.example.pathxplorer.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.CardCampusdetailBinding

class MajorAdapter(private val majorList: List<String>) :
    RecyclerView.Adapter<MajorAdapter.MajorViewHolder>() {

    inner class MajorViewHolder(private val binding: CardCampusdetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(major: String) {
            binding.tvMajorName.text = major
            binding.ivMajor.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MajorViewHolder {
        val binding = CardCampusdetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MajorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MajorViewHolder, position: Int) {
        holder.bind(majorList[position])
    }

    override fun getItemCount() = majorList.size
}