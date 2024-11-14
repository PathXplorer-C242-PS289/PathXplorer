package com.example.pathxplorer.ui.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.utils.Kampus

class GridAdapterCampus(private val listKampus: ArrayList<Kampus>) : RecyclerView.Adapter<GridAdapterCampus.GridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_campus_major, parent, false)
        return GridViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val kampus = listKampus[position]
        Glide.with(holder.itemView.context)
            .load(kampus.image)
            .apply(RequestOptions().override(350, 550))
            .into(holder.imgPhoto)
        holder.tvName.text = kampus.name
        holder.tvLocation.text = kampus.location
    }

    override fun getItemCount(): Int {
        return listKampus.size
    }

    inner class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_major_or_campus_name_grid)
        var tvLocation: TextView = itemView.findViewById(R.id.tv_faculty_or_campus_location_grid)
        var imgPhoto: ImageView = itemView.findViewById(R.id.iv_major_or_campus_image_grid)
    }
}