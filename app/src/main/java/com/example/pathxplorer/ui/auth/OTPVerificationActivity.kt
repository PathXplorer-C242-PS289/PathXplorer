package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pathxplorer.R
import com.example.pathxplorer.databinding.ActivityNewPasswordBinding
import com.example.pathxplorer.databinding.ActivityOtpverificationBinding

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpverificationBinding

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupAction()
    }

    private fun setupAction() {
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.edtCodeOtp.text.toString()
            Toast.makeText(this, "OTP code is correct", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}