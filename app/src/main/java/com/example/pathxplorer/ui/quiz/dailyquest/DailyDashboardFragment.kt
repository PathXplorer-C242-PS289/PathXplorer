package com.example.pathxplorer.ui.quiz.dailyquest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.FragmentDailyDashboardBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DailyDashboardFragment : Fragment() {

    private var _binding: FragmentDailyDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: FirebaseDatabase

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

        val messageRef = db.reference.child("messages")

        binding.startButton.setOnClickListener {
            val friendlyMessage = "Test"
            messageRef.push().setValue(friendlyMessage) { error, _ ->
                if (error != null) {
                    Toast.makeText(requireActivity(), "error" + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireActivity(), "succes", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.startButton.setOnClickListener {
            val dailyQuizFragment = DailyQuizFragment()
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, dailyQuizFragment, DailyQuizFragment::class.java.simpleName).commit()
        }
    }
}