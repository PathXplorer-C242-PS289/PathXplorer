package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
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

        setupVerificationButton(email)
    }

    private fun setupVerificationButton(email: String) {
        binding.btnSendOtp.setOnClickListener {
            val otp = binding.edtCodeOtp.text.toString()

            if (!isValidOtp(otp)) {
                showError(getString(R.string.invalid_otp_length))
                return@setOnClickListener
            }

            verifyOtp(email, otp)
        }
    }

    private fun verifyOtp(email: String, otp: String) {
        viewModel.verifyOtp(email, otp).observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> handleSuccessfulVerification()
                is Result.Error -> handleFailedVerification(result.error)
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

    private fun handleSuccessfulVerification() {
        showLoading(false)
        Toast.makeText(this, getString(R.string.otp_verification_success), Toast.LENGTH_SHORT).show()
        navigateToLogin()
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

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}