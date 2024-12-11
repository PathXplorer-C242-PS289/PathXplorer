package com.example.pathxplorer.ui.quiz.dailyquest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.pathxplorer.R
import com.example.pathxplorer.data.local.entity.DailyQuestEntity
import com.example.pathxplorer.data.models.DailyQuestQuestion
import com.example.pathxplorer.databinding.FragmentDailyDashboardBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Date

class DailyDashboardFragment : Fragment() {

    private var _binding: FragmentDailyDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseDatabase
    private lateinit var question: List<DailyQuestQuestion>

    private val viewModel by viewModels<DailyViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDailyDashboardBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Firebase.database

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.apply {
                    greetingText.text = getString(R.string.daily_quest_greeting, user.name)
                    pointsText.text = user.score.toString()
                }
            }

            viewModel.checkDaily(user.userId).let {
                if (it == 1) {
                    Toast.makeText(context, "Daily quest added", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Daily quest already added", Toast.LENGTH_SHORT).show()
                    val dailyEntity = DailyQuestEntity(
                        user.userId,
                        user.email,
                        Date().toString(),
                        0,
                        0
                    )
                    viewModel.insertDaily(dailyEntity)
                }
            }
        }

        val questionRef = db.getReference("daily_quest_question")

        questionRef.get().addOnSuccessListener {
            question = it.children.map { snapshot ->
                snapshot.getValue(DailyQuestQuestion::class.java)!!
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to get questions", Toast.LENGTH_SHORT).show()
        }

        setAction()
    }

    private fun setAction() {
        binding.startButton.setOnClickListener {
            val dailyQuizFragment = DailyQuizFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, dailyQuizFragment, DailyQuizFragment::class.java.simpleName).commit()
        }
    }
}