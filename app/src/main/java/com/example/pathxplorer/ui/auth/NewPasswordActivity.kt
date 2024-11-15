package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.ActivityForgotPasswordBinding
import com.example.pathxplorer.databinding.ActivityNewPasswordBinding

class NewPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.btnChangePassword.setOnClickListener {
            val password = binding.edtNewPassword.text.toString()
            val confirmPassword = binding.edtConfirmNewPassword.text.toString()
            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    // function to change password

                    // Password is correct
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    binding.edtConfirmNewPassword.error = "Password is not match"
                }
            } else {
                binding.edtNewPassword.error = "Password is required"
                binding.edtConfirmNewPassword.error = "Password is required"
            }
        }
    }
}