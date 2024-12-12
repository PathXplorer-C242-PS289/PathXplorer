package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.data.remote.response.RecommendationRiasecResponse
import com.example.pathxplorer.databinding.FragmentQuizResultBinding
import com.example.pathxplorer.ui.quiz.QuizViewModel
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: FirebaseDatabase

    private val viewModel by viewModels<QuizViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    companion object {
        const val RESULT_VALUE = "result_value"
        const val RIASEC_CODE = "riasec_code"
        const val POST_REF = "posts"
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

        db = Firebase.database

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

        binding.toolbar.setNavigationOnClickListener {
            gotoPostFragment(riasecCode!!)
        }

        onBackPressedCallback(riasecCode!!)

    }

    private fun setupResult(resultTest: RecommendationRiasecResponse) {
        val imageResource = when(resultTest.riasecType) {
            "R" -> R.drawable.r_type
            "I" -> R.drawable.i_type
            "A" -> R.drawable.a_type
            "S" -> R.drawable.s_type
            "E" -> R.drawable.e_type
            "C" -> R.drawable.c_type
            else -> R.drawable.r_type
        }
        with(binding) {

            typeImage.setImageResource(imageResource)
            tvTitle.text = getString(R.string.result_type, resultTest.riasecType)
            tvDescription.text = resultTest.interestDescription
            tvKeySkill.text = resultTest.keySkills
            tvExampleCareer.text = resultTest.exampleCareers

        }
    }


    private fun onBackPressedCallback(riasecType: String) {
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                gotoPostFragment(riasecType)
            }
        })
    }

    private fun gotoPostFragment(riasecType: String) {

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            val title = generateTitle()
            val post = TestResultPost(
                userId = user.userId,
                id = Timestamp.now().seconds.toInt()+user.userId,
                riasecType = riasecType,
                title = title,
                body = "Happy to share my result, I got $riasecType, what about you?",
                nameOwner = user.name,
                timestamp = Timestamp.now().toDate().toString()
            )

            MaterialAlertDialogBuilder(requireActivity())
                .setTitle("Share Your Result and Exit")
                .setMessage("Do you happy with your result?, Do you want to share your result?")
                .setPositiveButton("Yes") { dialog, which ->
                    val bundle = Bundle()
                    bundle.putParcelable("post", post)
                    val fragmentPost = PostFragment()
                    fragmentPost.arguments = bundle
                    val fragmentManager = parentFragmentManager
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragmentPost, PostFragment::class.java.simpleName).commit()
                }
                .setNegativeButton("No") { dialog, which ->
                    requireActivity().finish()
                }
                .show()
        }
    }

    private fun generateTitle(): String {
        val listTitle = listOf(
            "Hey this my result",
            "I got my result",
            "My result is here",
            "My result is ready",
            "I got my result"
        )
        return listTitle.random()
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