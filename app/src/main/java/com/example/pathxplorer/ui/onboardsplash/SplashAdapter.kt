package com.example.pathxplorer.ui.onboardsplash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.data.models.OnboardingItem
import com.example.pathxplorer.databinding.ActivitySplashItemBinding

class SplashAdapter(
    private val onboardingItems: List<OnboardingItem>
) : RecyclerView.Adapter<SplashAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        return OnboardingViewHolder(
            ActivitySplashItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return onboardingItems.size
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(onboardingItems[position])
    }

    inner class OnboardingViewHolder(
        private val binding: ActivitySplashItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(onboardingItem: OnboardingItem) {
            binding.apply {
                imageSplash.setImageResource(onboardingItem.image)
                textTitle.text = onboardingItem.title
                textDescription.text = onboardingItem.description
            }
        }
    }
}