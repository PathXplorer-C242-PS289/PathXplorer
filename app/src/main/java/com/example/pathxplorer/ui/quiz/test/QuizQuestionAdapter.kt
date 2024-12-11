package com.example.pathxplorer.ui.quiz.test

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.Question
import com.example.pathxplorer.databinding.QuestionItemBinding


class QuizQuestionAdapter: ListAdapter<Question, QuizQuestionAdapter.QuizAdapterViewHolder>(
    DIFF_CALLBACK
) {

    private lateinit var onAnswerClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(answer: Int, position: Question)
    }

    fun setOnClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onAnswerClickCallback = onItemClickCallback
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Question> =
            object : DiffUtil.ItemCallback<Question>() {
                override fun areItemsTheSame(oldItem: Question, newItem: Question): Boolean {
                    return oldItem.page == newItem.page
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldItem: Question, newItem: Question): Boolean {
                    return oldItem == newItem
                }
            }
    }

    class QuizAdapterViewHolder(binding: QuestionItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val question = binding.questionTextView
        val radioGroup = binding.answersRadioGroup
        val checkedIndicator = binding.checkedIndicator

        fun bind(question: Question) {
            this.question.text = question.question
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizAdapterViewHolder {
        val binding = QuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuizAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuizAdapterViewHolder, position: Int) {
        holder.bind(getItem(position))

        if (getItem(position).isChecked) {
            holder.radioGroup.check(
                when (getItem(position).value) {
                    1 -> R.id.answer_1
                    2 -> R.id.answer_2
                    3 -> R.id.answer_3
                    4 -> R.id.answer_4
                    else -> R.id.answer_5
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
                0 -> 1
                1 -> 2
                2 -> 3
                3 -> 4
                else -> 5
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