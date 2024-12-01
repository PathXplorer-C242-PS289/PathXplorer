package com.example.pathxplorer.ui.quiz.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.MainActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.Answer
import com.example.pathxplorer.data.models.Question
import com.example.pathxplorer.databinding.FragmentQuizBinding
import com.example.pathxplorer.ui.quiz.QuizQuestionAdapter
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.utils.CustomDialog

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<QuizViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.isEnabled = false

//        onBackPressedCallback()

        setupQuestion()
    }

    private fun setupQuestion() {
        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.layoutManager = linearLayoutManager
        val questionAdapter = QuizQuestionAdapter()
        binding.recyclerView.adapter = questionAdapter

        viewModel.indexedValue.observe(viewLifecycleOwner) { index ->
            questionAdapter.submitList(viewModel.questions[index])
            val isEnd = viewModel.isEndOfQuestion(viewModel.questions)

            setupAction(isEnd)
            binding.progressNumber.text =
                getString(R.string.index_question, (index + 1).toString(), viewModel.questions.size.toString())
        }

        questionAdapter.setOnClickCallback(object : QuizQuestionAdapter.OnItemClickCallback {
            override fun onItemClicked(answer: Int, position: Question) {
                setupAnswer(answer, position)
            }
        })
    }

    private fun setupAnswer(answer: Int, position: Question) {
        val answerByQuestion = Answer(
            position.question,
            position.number,
            answer
        )

        viewModel.addAnswer(answerByQuestion)

        with(binding.submitButton) {
            isEnabled = viewModel.answerSize == viewModel.questions[viewModel.indexedValue.value!!].size
        }

        val progress = viewModel.progress(position.number)

        binding.progressIndicator.progress = ((progress * 100)) / viewModel.questions[viewModel.indexedValue.value!!].size
    }

    private fun setupAction(isEnd: Boolean) {
        binding.submitButton.text = if (isEnd) "Submit" else "Next"

        binding.submitButton.setOnClickListener {
            if (isEnd) {
                val result = viewModel.resultAnswer()
                val bundle = Bundle()
                bundle.putIntegerArrayList(QuizResultFragment.RESULT_VALUE, result)

                val fragmentResult = QuizResultFragment()
                fragmentResult.arguments = bundle
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragmentResult, QuizResultFragment::class.java.simpleName).commit()
            } else {
                viewModel.addIndexedValue()
                binding.submitButton.isEnabled = false
                binding.progressIndicator.progress = 0
            }
        }
    }

//    private fun onBackPressedCallback() {
//        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                CustomDialog().showDialog(requireActivity(), "Exit Quiz", "If you exit, your progress will be lost. Are you sure you want to exit?", "Yes", "No") { positive ->
//                    if (positive) {
//                        Log.d("QuizFragment", "Exit the quiz")
//                    } else {
//                        Log.e("QuizFragment", "Can't exit the quiz")
//                    }
//                }
//
////                val intent = Intent(context, MainActivity::class.java)
////                startActivity(intent)
//            }
//        })
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}