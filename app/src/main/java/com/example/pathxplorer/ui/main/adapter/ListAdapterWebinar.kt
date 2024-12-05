package com.example.pathxplorer.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.CardWebinarBinding

class ListAdapterWebinar(
    private var listWebinar: ArrayList<WebinarModel>,
    private val onWebinarClick: (WebinarModel) -> Unit
) : RecyclerView.Adapter<ListAdapterWebinar.ListViewHolder>() {

    inner class ListViewHolder(private val binding: CardWebinarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(webinar: WebinarModel) {
            binding.apply {
                tvWebinarTitle.text = webinar.name
                tvWebinarDate.text = webinar.beginTime?.split(" ")?.firstOrNull() ?: ""

                Glide.with(itemView.context)
                    .load(webinar.imageLogo)
                    .placeholder(R.drawable.webinar)
                    .error(R.drawable.webinar)
                    .into(ivWebinarImage)

                root.setOnClickListener { onWebinarClick(webinar) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = CardWebinarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listWebinar[position])
    }

    override fun getItemCount(): Int = listWebinar.size

    fun updateData(newData: ArrayList<WebinarModel>) {
        val diffCallback = WebinarDiffCallback(listWebinar, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listWebinar.clear()
        listWebinar.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    private class WebinarDiffCallback(
        private val oldList: List<WebinarModel>,
        private val newList: List<WebinarModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}