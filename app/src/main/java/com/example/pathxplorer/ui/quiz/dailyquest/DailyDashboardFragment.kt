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
import com.example.pathxplorer.databinding.FragmentDailyDashboardBinding
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyDashboardFragment : Fragment() {

    private var _binding: FragmentDailyDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseDatabase

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
                }
            }

            viewModel.dailyQuest.observe(viewLifecycleOwner) { dailyQuest ->
                if (dailyQuest == null) {
                    val dailyQuestEntity = DailyQuestEntity(
                        idUser =  user.userId,
                        emailUser = user.email,
                        lastCheck = Date().toString(),
                        dailyQuestCount = 1,
                        score = 0,
                    )
                    viewModel.insertDaily(dailyQuestEntity)
                } else {
                    setDaily(user.userId)
                }
            }

            setDaily(user.userId)

            if (isDateGreaterThanNow(binding.lastChecked.text.toString())) {
                viewModel.updateDailyQuestCount(user.userId)
                setAction()
            } else {
                Toast.makeText(context, "You have checked today", Toast.LENGTH_SHORT).show()
            }
        }

        setAction()
    }

    private fun isDateGreaterThanNow(dateString: String): Boolean {
        if (dateString.isEmpty()) return true
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val date = dateFormat.parse(dateString)
        val currentDate = Date()
        return date?.after(currentDate) ?: false
    }

    private fun setDaily(userId: Int) {
        viewModel.getDailyQuestById(userId)
        viewModel.dailyQuest.observe(viewLifecycleOwner) { dailyQuest ->
            if (dailyQuest == null) {
                val dailyQuestEntity = DailyQuestEntity(
                    idUser = userId,
                    emailUser = "",
                    lastCheck = Date().toString(),
                    dailyQuestCount = 1,
                    score = 0,
                )
                viewModel.insertDaily(dailyQuestEntity)
            } else {
                with(binding) {
//                dailyQuest.score.toString().also { pointsText.text = it }
                    pointsText.text = dailyQuest?.score.toString()
                    lastChecked.text = formatDate(dailyQuest.lastCheck)
                    dailyQuest.dailyQuestCount.toString().also { strikes.text = it }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun formatDate(date: String): String {
        val dateResult = Date(date)
        val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return format.format(dateResult)
    }

    private fun setAction() {
        binding.startButton.setOnClickListener {
            val bundle = Bundle()
//            bundle.putParcelableArrayList(DailyQuizFragment.EXTRA_QUESTION_DAILY, questions)
            val dailyQuizFragment = DailyQuizFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().replace(R.id.container_daily, dailyQuizFragment, DailyQuizFragment::class.java.simpleName).commit()
        }
    }
}