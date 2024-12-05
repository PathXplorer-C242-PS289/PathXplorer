package com.example.pathxplorer.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.CardWebinarBinding

class WebinarAdapter(
    private val onWebinarClick: (WebinarModel) -> Unit
) : ListAdapter<WebinarModel, WebinarAdapter.WebinarViewHolder>(WebinarDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebinarViewHolder {
        return WebinarViewHolder(
            CardWebinarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: WebinarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WebinarViewHolder(
        private val binding: CardWebinarBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(webinar: WebinarModel) {
            binding.apply {
                tvWebinarTitle.text = webinar.name
                tvWebinarDate.text = webinar.beginTime?.split(" ")?.get(0) ?: ""

                Glide.with(root.context)
                    .load(webinar.imageLogo)
                    .placeholder(R.drawable.webinar)
                    .error(R.drawable.webinar)
                    .into(ivWebinarImage)

                root.setOnClickListener { onWebinarClick(webinar) }
            }
        }
    }

    private class WebinarDiffCallback : DiffUtil.ItemCallback<WebinarModel>() {
        override fun areItemsTheSame(oldItem: WebinarModel, newItem: WebinarModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: WebinarModel, newItem: WebinarModel): Boolean {
            return oldItem == newItem
        }
    }
}