package com.example.pathxplorer.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.databinding.PostingTestResultBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PostingTestAdapter(
    options: FirebaseRecyclerOptions<TestResultPost>
) : FirebaseRecyclerAdapter<TestResultPost, PostingTestAdapter.PostingViewHolder>(options) {
    class PostingViewHolder(private val binding: PostingTestResultBinding):
    RecyclerView.ViewHolder(binding.root) {
        fun bind(post: TestResultPost) {
            binding.title.text = post.title
            binding.body.text = post.body
            when(post.riasecType) {
                "R" -> binding.ivPostingTestResult.setImageResource(R.drawable.r_type)
                "I" -> binding.ivPostingTestResult.setImageResource(R.drawable.i_type)
                "A" -> binding.ivPostingTestResult.setImageResource(R.drawable.a_type)
                "S" -> binding.ivPostingTestResult.setImageResource(R.drawable.s_type)
                "E" -> binding.ivPostingTestResult.setImageResource(R.drawable.e_type)
                "C" -> binding.ivPostingTestResult.setImageResource(R.drawable.c_type)
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = PostingTestResultBinding.inflate(inflater, parent, false)
        return PostingViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: PostingViewHolder,
        position: Int,
        model: TestResultPost
    ) {
        holder.bind(model)
    }


}