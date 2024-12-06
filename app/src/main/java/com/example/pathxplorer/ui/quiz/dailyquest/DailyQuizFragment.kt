package com.example.pathxplorer.ui.quiz.dailyquest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.FragmentDailyQuizBinding

class DailyQuizFragment : Fragment() {

    private var _binding: FragmentDailyQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDailyQuizBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val resultFragment = DailyResultFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, resultFragment, DailyResultFragment::class.java.simpleName).commit()
        }
    }

}