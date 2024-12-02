package com.example.pathxplorer.ui.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.databinding.ResultItemBinding

class ResultAdapter(private val listResult: MutableMap<Int, Int>): RecyclerView.Adapter<ResultAdapter.ResultViewHolder>() {
    class ResultViewHolder(binding: ResultItemBinding): RecyclerView.ViewHolder(binding.root) {
        private val indicator = binding.linearProgressIndicator
        private val value = binding.valueText

        fun bind(value: Int, key: Int, length: Int) {
            this.value.text = "$key"
            this.indicator.progress = (value * 100) / length
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val view: ResultItemBinding = ResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultViewHolder(view)
    }

    override fun getItemCount(): Int = listResult.size

    private fun getLengthResult(): Int {
        var length = 0
        for (i in listResult.values) {
            length += i
        }
        return length
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val key = listResult.keys.elementAt(position)
        val value = listResult.values.elementAt(position)
        val length = getLengthResult()
        holder.bind(value, key, length)
    }
}