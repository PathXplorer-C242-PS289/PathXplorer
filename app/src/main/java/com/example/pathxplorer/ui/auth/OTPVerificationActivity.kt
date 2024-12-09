package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.databinding.ActivityOtpverificationBinding
import com.example.pathxplorer.ui.utils.AuthViewModelFactory

class OTPVerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpverificationBinding


    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpverificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        val email = intent.getStringExtra(EXTRA_EMAIL) ?: run {
            showError(getString(R.string.email_not_found))
            finish()
            return
        }

        val type = intent.getStringExtra(TYPE_OTP) ?: run {
            showError(getString(R.string.not_found_type_key))
            finish()
            return
        }

        binding.emailDestination.text = getString(R.string.email_destination, email)

        setupVerificationButton(email, type)
    }

    private fun setupVerificationButton(email: String, type: String) {
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.edtCodeOtp.text.toString()

            if (!isValidOtp(otp)) {
                showError(getString(R.string.invalid_otp_length))
                return@setOnClickListener
            }

            verifyOtp(email, otp, type)
        }

        binding.tvResend.setOnClickListener {
            viewModel.resendOtp(email).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> Toast.makeText(this, "OTP Sukses Terkirim", Toast.LENGTH_SHORT).show()
                    is Result.Error -> handleFailedVerification(result.error)
                }
            }
        }
    }

    private fun verifyOtp(email: String, otp: String, type: String) {
        if (type == "RegisterAccount") {
            viewModel.verifyOtp(email, otp).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> handleSuccessfulVerification(type, email, otp)
                    is Result.Error -> handleFailedVerification(result.error)
                }
            }
        } else if (type == "ForgotPassword") {
            viewModel.verifyOtpForgotPassword(email, otp).observe(this) { result ->
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> handleSuccessfulVerification(type, email, otp)
                    is Result.Error -> handleFailedVerification(result.error)
                }
            }
        }
    }

    private fun isValidOtp(otp: String): Boolean = otp.length == 6

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            btnSendOtp.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun handleSuccessfulVerification(type: String, email: String, otp: String) {
        showLoading(false)
        Toast.makeText(this, getString(R.string.otp_verification_success), Toast.LENGTH_SHORT).show()
        Log.d("OTPVerificationActivity", "type: $type")
        if (type == "RegisterAccount") {
            navigateToLogin()
        } else if (type == "ForgotPassword") {
            navigateToNewPassword(email, otp)
        }
    }

    private fun handleFailedVerification(error: String?) {
        showLoading(false)
        showError(error ?: getString(R.string.otp_verification_failed))
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToNewPassword(email: String, otp: String) {
        startActivity(Intent(this, NewPasswordActivity::class.java)
            .putExtra(NewPasswordActivity.EXTRA_EMAIL, email)
            .putExtra(NewPasswordActivity.EXTRA_OTP, otp))
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val TYPE_OTP = "otp"
    }
}