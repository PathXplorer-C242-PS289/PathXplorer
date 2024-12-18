package com.example.pathxplorer.ui.quiz.dailyquest

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.DailyQuestQuestion
import com.example.pathxplorer.databinding.FragmentDailyQuizBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DailyQuizFragment : Fragment() {

    private var _binding: FragmentDailyQuizBinding? = null
    private val binding get() = _binding!!
    private lateinit var question: ArrayList<DailyQuestQuestion>

    private lateinit var db: FirebaseDatabase

    private val viewModel by viewModels<DailyViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    companion object {
        const val TAG = "DailyQuizFragment"
        const val EXTRA_QUESTION_DAILY = "extra_question_daily"
        const val QUESTION_REF = "question_daily"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDailyQuizBinding.inflate(inflater, container, false)
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        val adapter = DailyQuestAdapter()
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        db = Firebase.database

//        adapter.submitList(question.take(5))

        binding.submitButton.isEnabled = false

        val questionRef = db.getReference(QUESTION_REF)
        questionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    question = arrayListOf()
                    for (data in snapshot.children) {
                        val item = data.getValue(DailyQuestQuestion::class.java)
                        item?.let { question.add(it) }
                    }
                    question = question.shuffled().take(3) as ArrayList<DailyQuestQuestion>
                    adapter.submitList(question)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error Ocurated : ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })

        adapter.setOnClickCallback(object : DailyQuestAdapter.OnItemClickCallback {
            @SuppressLint("UseCompatLoadingForDrawables")
            override fun onItemClicked(answer: Int, position: DailyQuestQuestion) {
                question[question.indexOf(position)].isChecked = true
                question[question.indexOf(position)].value = answer
                question[question.indexOf(position)].isCorrect = answer == position.correctAnswer

                Log.d("DailyQuizFragment", "onItemClicked: $question")
                viewModel.progressQuizDaily(question)
                viewModel.progressQuiz.observe(viewLifecycleOwner) { progress ->
                    if (progress == question.size) {
                        binding.submitButton.isEnabled = true
                        @Suppress("DEPRECATION")
                        binding.progressImage.background = resources.getDrawable(R.color.blue_700)
                    }

                    binding.progressIndicator.progress = progress * 100 / question.size
                }
            }
        })

        binding.submitButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putIntegerArrayList(DailyResultFragment.RESULT, setResultDailyTest(question))
            val resultFragment = DailyResultFragment()
            resultFragment.arguments = bundle
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().replace(R.id.container_daily, resultFragment, DailyResultFragment::class.java.simpleName).commit()
        }

        onBackPressedCallback()
    }

    private fun setResultDailyTest(questions: ArrayList<DailyQuestQuestion>): ArrayList<Int>{

        val correctAnswer = questions.filter { it.isCorrect == true }
        val result = arrayListOf(
            correctAnswer.size,
            questions.size
        )
        return result

    }

    private fun onBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Cancel Quiz")
                    .setMessage("Are you sure you want to cancel the quiz?")
                    .setPositiveButton("Yes") { _, _ ->
                        requireActivity().finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                dialog.show()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}