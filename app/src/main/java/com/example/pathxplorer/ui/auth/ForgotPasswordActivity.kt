package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.databinding.ActivityForgotPasswordBinding
import com.example.pathxplorer.ui.utils.AuthViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
    }

    private fun setupAction() {
        binding.btnSendRequestPassword.setOnClickListener {
            val email = binding.edtEmailRecovery.text.toString()
            if (email.isNotEmpty()) {
                viewModel.forgotPassword(email).observe(this) { result ->
                    when(result) {
                        is Result.Loading -> {
                            // Show loading
                        }
                        is Result.Error -> {

                        }
                        is Result.Success -> {
                            Toast.makeText(this, "Request password sent to $email", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, OTPVerificationActivity::class.java)
                            intent.putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
                            intent.putExtra(OTPVerificationActivity.TYPE_OTP, "ForgotPassword")
                            startActivity(intent)
                        }
                    }
                }
            } else {
                binding.edtEmailRecovery.error = "Email is required"
            }
        }
    }
}