package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.databinding.ActivityNewPasswordBinding
import com.example.pathxplorer.databinding.ActivityOtpverificationBinding
import com.example.pathxplorer.ui.utils.AuthViewModelFactory
import kotlinx.coroutines.launch

class OTPVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpverificationBinding

    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val email = intent.getStringExtra(EXTRA_EMAIL)

        setupAction(email!!)
    }

    private fun setupAction(email: String) {
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.edtCodeOtp.text.toString()

            if (otp.length != 6) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage("OTP code must be 6 digits")
                    setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create()
                    show()
                }
            }

            lifecycleScope.launch {
                viewModel.sendOtp(email, otp).observe(this@OTPVerificationActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this@OTPVerificationActivity, "Loading...", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            Toast.makeText(this@OTPVerificationActivity, "OTP code is correct", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@OTPVerificationActivity, LoginActivity::class.java))
                        }
                        is Result.Error -> {
                            Toast.makeText(this@OTPVerificationActivity, "OTP code is incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

//            Toast.makeText(this, "OTP code is correct", Toast.LENGTH_SHORT).show()
//
//            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}