package com.example.pathxplorer.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.ui.utils.Kampus

class CarouselAdapter(
    private val listKampus: ArrayList<Kampus>,
    private val onClick: (Kampus) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    class CarouselViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivImage: ImageView = itemView.findViewById(R.id.iv_major_or_campus_image)
        val tvName: TextView = itemView.findViewById(R.id.tv_major_or_campus_name)
        val tvFaculty: TextView = itemView.findViewById(R.id.tv_faculty_or_campus_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.card_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun getItemCount(): Int = listKampus.size

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val (name, location, image) = listKampus[position]
        holder.ivImage.setImageResource(image)
        holder.tvName.text = name
        holder.tvFaculty.text = location

        holder.itemView.setOnClickListener {
            onClick(listKampus[position])
        }
    }
}