package com.example.pathxplorer.ui.quiz.test

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.pathxplorer.MainActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.TestResultPost
import com.example.pathxplorer.databinding.FragmentPostBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.MainScope

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

        val postReceiver = arguments?.getParcelable(POST) as TestResultPost?

        db = Firebase.database
        with(binding) {
            title.text = postReceiver?.title
            body.text = postReceiver?.body
            time.text = postReceiver?.timestamp
            ownerName.text = postReceiver?.nameOwner
            when (postReceiver?.riasecType) {
                "R" -> riasecType.setImageResource(R.drawable.r_type)
                "I" -> riasecType.setImageResource(R.drawable.i_type)
                "A" -> riasecType.setImageResource(R.drawable.a_type)
                "S" -> riasecType.setImageResource(R.drawable.s_type)
                "E" -> riasecType.setImageResource(R.drawable.e_type)
                "C" -> riasecType.setImageResource(R.drawable.c_type)
            }

            edtTitle.setText(postReceiver?.title)
            edtBody.setText(postReceiver?.body)

            edtTitle.addTextChangedListener {
                title.text = it.toString()
            }

            edtBody.addTextChangedListener {
                body.text = it.toString()
            }

            btnHome.setOnClickListener {
                requireActivity().finish()
            }
            btnShare.setOnClickListener {
                val postSend = TestResultPost(
                    userId = postReceiver?.userId,
                    id = postReceiver?.id,
                    riasecType = postReceiver?.riasecType!!,
                    title = edtTitle.text.toString(),
                    body = edtBody.text.toString(),
                    nameOwner = postReceiver.nameOwner,
                    timestamp = postReceiver.timestamp,
                )
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Post")
                    .setMessage("Are you sure you want to post this result?")
                    .setPositiveButton("Yes") { _, _ ->
                        postingResult(postSend)
                        binding.btnShare.isEnabled = false
                        Log.d(TAG, "Post result success")
                        requireActivity().finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
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