package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.FragmentQuizRulesBinding

class QuizRulesFragment : Fragment() {

    private var _binding: FragmentQuizRulesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizRulesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startQuizButton.setOnClickListener {
            it.findNavController().navigate(R.id.action_quizRulesFragment_to_quizFragment)
        }
    }

}