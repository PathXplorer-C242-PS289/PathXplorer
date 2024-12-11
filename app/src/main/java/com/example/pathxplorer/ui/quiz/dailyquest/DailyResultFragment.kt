package com.example.pathxplorer.ui.quiz.dailyquest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.FragmentDailyResultBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory

class DailyResultFragment : Fragment() {

    private lateinit var _binding: FragmentDailyResultBinding
    private val binding get() = _binding

    private val viewModel by viewModels<DailyViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = arguments?.getIntegerArrayList(RESULT)

        val score = (result?.get(0)?.toDouble()?.div(result[1]!!))?.times(100)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                viewModel.updateScore(user.userId, score?.toInt()!!)
            }
        }

        setView(score?.toInt()!!, result)
    }

    private fun setView(score: Int, result: ArrayList<Int>) {
        with(binding) {
            "You got $score points".also { pointPlus.text = it }
            "Your result is ${result[0]} out of ${result[1]}".also { resultDescriptionTextView.text = it }
        }
    }

    companion object {
        const val TAG = "DailyResultFragment"
        const val RESULT = "result"
    }
}