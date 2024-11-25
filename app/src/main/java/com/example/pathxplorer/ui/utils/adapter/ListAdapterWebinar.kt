package com.example.pathxplorer.ui.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.utils.Webinar

class ListAdapterWebinar(private val listWebinar: ArrayList<Webinar>): RecyclerView.Adapter<ListAdapterWebinar.ListViewHolder>() {
    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.iv_major_or_campus_image)
        val tvWebinarDate: TextView = itemView.findViewById(R.id.tv_faculty_or_campus_location)
        val tvWebinarTitle: TextView = itemView.findViewById(R.id.tv_major_or_campus_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_webinar, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listWebinar.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, date, image) = listWebinar[position]
        holder.ivImage.setImageResource(image)
        holder.tvWebinarTitle.text = name
        holder.tvWebinarDate.text = date
    }
}