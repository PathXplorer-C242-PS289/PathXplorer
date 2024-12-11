package com.example.pathxplorer.ui.quiz.dailyquest

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.DailyQuestQuestion
import com.example.pathxplorer.data.models.Question
import com.example.pathxplorer.databinding.DailyQuizItemBinding

class DailyQuestAdapter: ListAdapter<DailyQuestQuestion, DailyQuestAdapter.ViewHolder>(DIFF_CALLBACK) {

    private lateinit var onAnswerClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(answer: Int, position: DailyQuestQuestion)
    }

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onAnswerClickCallback = onItemClickCallback
    }

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

        if (getItem(position).isChecked) {
            holder.radioGroup.check(
                when (getItem(position).value) {
                    0 -> R.id.answer_1
                    1 -> R.id.answer_2
                    2 -> R.id.answer_3
                    else -> R.id.answer_4
                }
            )
            holder.checkedIndicator.setImageResource(R.drawable.ic_checked)
        } else {
            holder.radioGroup.setOnCheckedChangeListener(null)
            holder.radioGroup.clearCheck()
            holder.checkedIndicator.setImageResource(R.drawable.ic_unchecked)
        }

        holder.radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            val radioChecked = radioGroup.findViewById<RadioButton>(i)
            val indexItem = radioGroup.indexOfChild(radioChecked)
            val valueAnswer = when (indexItem) {
                0 -> 0
                1 -> 1
                2 -> 2
                else -> 3
            }

            val item = getItem(position)

            if (item.correctAnswer == valueAnswer) {
                item.isCorrect = true
            } else {
                item.isCorrect = false
            }

            holder.checkedIndicator.setImageResource(
                if (radioChecked.isChecked) {
                    R.drawable.ic_checked
                } else {
                    R.drawable.ic_unchecked
                }
            )
            onAnswerClickCallback.onItemClicked(valueAnswer, getItem(position))
        }
    }


}