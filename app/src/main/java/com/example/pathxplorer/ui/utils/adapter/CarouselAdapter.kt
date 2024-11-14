package com.example.pathxplorer.ui.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.utils.Kampus


// this is nessasary before API from CC is Complete
class CarouselAdapter(private val listKampus: ArrayList<Kampus>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {
    class CarouselViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.iv_major_or_campus_image)
        val tvName: TextView = itemView.findViewById(R.id.tv_major_or_campus_name)
        val tvFaculty: TextView = itemView.findViewById(R.id.tv_faculty_or_campus_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listKampus.size
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val (name, description, photo) = listKampus[position]
        holder.ivImage.setImageResource(photo)
        holder.tvName.text = name
        holder.tvFaculty.text = description
    }


}