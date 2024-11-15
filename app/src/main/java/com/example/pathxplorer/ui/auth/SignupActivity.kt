package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val email = binding.labelEmail.text.toString()
//
//            AlertDialog.Builder(this).apply {
//                setTitle("Yeah!")
//                setMessage("Akun dengan $email sudah jadi nih. Yuk, login dan belajar coding.")
//                setPositiveButton("Lanjut") { _, _ ->
//                    finish()
//                }
//                create()
//                show()
//            }
            val intent = Intent(this, OTPVerificationActivity::class.java)
            intent.putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
            startActivity(intent)
        }

        binding.tvDaftar.setOnClickListener {
            finish()
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val dialog = AlertDialog.Builder(this).apply {
                setTitle("Privacy Policy")
                setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                setMessage(R.string.privacy_policy_content)
            }
            dialog.create()
            dialog.show()
        }
    }
}