package com.example.pathxplorer.ui.quiz.dailyquest

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.data.models.DailyQuestQuestion
import com.example.pathxplorer.databinding.DailyQuizItemBinding

class DailyQuestAdapter: ListAdapter<DailyQuestQuestion, DailyQuestAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DailyQuestQuestion> =
            object : DiffUtil.ItemCallback<DailyQuestQuestion>() {
                override fun areItemsTheSame(oldItem: DailyQuestQuestion, newItem: DailyQuestQuestion): Boolean {
                    return oldItem.question == newItem.question
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: DailyQuestQuestion, newItem: DailyQuestQuestion): Boolean {
                    return oldItem == newItem
                }
            }
    }

    class ViewHolder(binding: DailyQuizItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val question = binding.questionTextView
        val radioGroup = binding.answersRadioGroup
        val checkedIndicator = binding.checkedIndicator

        fun bind(question: DailyQuestQuestion) {
            this.question.text = question.question
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DailyQuizItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}