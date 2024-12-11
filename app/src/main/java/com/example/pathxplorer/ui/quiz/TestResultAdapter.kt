package com.example.pathxplorer.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.data.remote.response.TestResultsItem
import com.example.pathxplorer.databinding.TestsItemBinding

class TestResultAdapter(
    private val onItemClick: (TestResultsItem) -> Unit
) : ListAdapter<TestResultsItem, TestResultAdapter.TestViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TestResultsItem>() {
            override fun areItemsTheSame(oldItem: TestResultsItem, newItem: TestResultsItem): Boolean {
                return oldItem.testId == newItem.testId
            }

            override fun areContentsTheSame(oldItem: TestResultsItem, newItem: TestResultsItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TestViewHolder(private val binding: TestsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(test: TestResultsItem) {

            binding.testTitle.text = test.riasecType
            binding.testDescription.text = test.interestDescription
            binding.careerRecommendation.text = test.exampleCareers
            binding.tvKeySkills.text = test.keySkills

            binding.root.setOnClickListener {
                onItemClick(test)

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
        val binding = TestsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
