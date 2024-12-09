package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.databinding.FragmentQuizResultBinding
import com.example.pathxplorer.service.riasec.RiasecHelper
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.quiz.ResultAdapter
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import kotlinx.coroutines.launch

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<QuizViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    companion object {
        const val RESULT_VALUE = "result_value"
        const val RIASEC_CODE = "riasec_code"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().finish()
        }

        val resultVal = arguments?.getIntegerArrayList(RESULT_VALUE)
        val riasecCode =  arguments?.getString(RIASEC_CODE)

        lifecycleScope.launch {
            viewModel.getRecommendation(riasecCode!!).observe(viewLifecycleOwner) { result ->
                        Log.d("Result", result.toString())
                        when (result) {
                            is Result.Loading -> {
                                Log.d("Result", "Loading")
                            }
                            is Result.Success -> {
                                Log.d("Result Test", "${result.data}")

                                setupResult(result.data)
                            }
                            is Result.Error -> {
                                Log.d("Result Error", result.error.toString())
                            }
                        }
                    }
        }

        val result = setResultKey(resultVal!!)

        onBackPressedCallback()

        // view
//        setupResult(result)
    }

    private fun setupResult(resultTest: RecommendationRiasecResponse) {
        with(binding) {
            tvTitle.text = "Your type is ${resultTest.riasecType}"
            tvDescription.text = "*What is meanns of your type* \n ${resultTest.interestDescription}"
            tvKeySkill.text = "*Your key skills :* ${resultTest.keySkills}"
            tvExampleCareer.text = "*What you can be :* ${resultTest.exampleCareers}"
        }
    }

//    private fun setupResult(list: MutableMap<Int, Int>) {
//        binding.rvResultQuiz.layoutManager = LinearLayoutManager(requireActivity())
//        binding.rvResultQuiz.setHasFixedSize(true)
//        val adapter = ResultAdapter(list)
//        binding.rvResultQuiz.adapter = adapter
//    }

    private fun onBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    private fun setResultKey(result: ArrayList<Int>): MutableMap<Int, Int> {
        val map = mutableMapOf<Int, Int>()
        for (i in 0..<result.size) {
            for (j in 1..5) {
                if (result[i] == j) {
                    if (map[j]==null) {
                        map[j] = 1
                    } else {
                        map[j] = map[j]!! + 1
                    }
                } else {
                    if (map[j]==null) {
                        map[j] = 0
                    }
                }
            }
        }
        Log.d("Result", map.toString())
        return map
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}