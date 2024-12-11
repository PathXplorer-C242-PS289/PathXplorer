package com.example.pathxplorer.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.databinding.TestsItemBinding

class TestResultAdapter: ListAdapter<TestResultsItem, TestResultAdapter.TestViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TestResultsItem>() {
            override fun areItemsTheSame(
                oldItem: TestResultsItem,
                newItem: TestResultsItem
            ): Boolean {
                return oldItem.testId == newItem.testId
            }

            override fun areContentsTheSame(
                oldItem: TestResultsItem,
                newItem: TestResultsItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TestViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var binding = TestsItemBinding.bind(itemView)

        fun bind(test: TestResultsItem) {

            val imageResource = when(test.riasecType) {
                "R" -> R.drawable.r_type
                "I" -> R.drawable.i_type
                "A" -> R.drawable.a_type
                "S" -> R.drawable.s_type
                "E" -> R.drawable.e_type
                "C" -> R.drawable.c_type
                else -> R.drawable.r_type
            }

            with(binding) {
                testImage.setImageResource(imageResource)
                testDescription.text = test.interestDescription
                careerRecommendation.text = test.exampleCareers
                tvKeySkills.text = test.keySkills
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.tests_item, parent, false)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}