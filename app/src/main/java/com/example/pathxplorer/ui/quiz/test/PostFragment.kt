package com.example.pathxplorer.ui.quiz.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.databinding.FragmentPostBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PostFragment : Fragment() {

    private lateinit var _binding: FragmentPostBinding
    private val binding get() = _binding

    private lateinit var db: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val post = arguments?.getParcelable(POST) as TestResultPost?

        db = Firebase.database
        with(binding) {
            title.text = post?.title
            body.text = post?.body
            riasecType.text = post?.riasecType
            btnHome.setOnClickListener {
                requireActivity().finish()
            }
            btnShare.setOnClickListener {
                postingResult(post!!)
                btnShare.isEnabled = false
            }
        }
    }

    private fun postingResult(post:TestResultPost) {
        val postRef = db.getReference(POST_REF)
        val postId = postRef.push().key
        postRef.child(postId!!).setValue(post)
    }

    companion object {
        const val TAG = "PostFragment"
        const val POST = "post"
        const val POST_REF = "posts"
    }
}