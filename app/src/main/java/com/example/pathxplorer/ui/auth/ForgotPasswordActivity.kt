package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

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
            // Send request password
            val email = binding.edtEmailRecovery.text.toString()
            if (email.isNotEmpty()) {
                Toast.makeText(this, "Request password sent to $email", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NewPasswordActivity::class.java)
                startActivity(intent)
            } else {
                binding.edtEmailRecovery.error = "Email is required"
            }
        }
    }
}