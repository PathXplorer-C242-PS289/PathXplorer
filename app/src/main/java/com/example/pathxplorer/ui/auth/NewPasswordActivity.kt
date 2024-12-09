package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.databinding.ActivityForgotPasswordBinding
import com.example.pathxplorer.databinding.ActivityNewPasswordBinding
import com.example.pathxplorer.ui.utils.AuthViewModelFactory

class NewPasswordActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        const val EXTRA_OTP = "extra_otp"
    }

    private lateinit var binding: ActivityNewPasswordBinding
    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChangePassword.isEnabled = false

        val email = intent.getStringExtra(EXTRA_EMAIL) ?: run {
            showError(getString(R.string.email_not_found))
            finish()
            return
        }

        val otp = intent.getStringExtra(EXTRA_OTP) ?: run {
            showError(getString(R.string.otp_not_found))
            finish()
            return
        }

        supportActionBar?.hide()

        setupAction(otp, email)
    }

    private fun setupAction(otp: String, email: String) {

        // on change edt text confirm
        binding.edtConfirmNewPassword.addTextChangedListener {
            val password = binding.edtNewPassword.text.toString()
            val confirmPassword = binding.edtConfirmNewPassword.text.toString()

            if (binding.edtConfirmNewPassword.error != null) {
                binding.btnChangePassword.isEnabled = false
            }

            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    binding.btnChangePassword.isEnabled = true
                } else {
                    binding.btnChangePassword.isEnabled = false
                }
            } else {
                binding.btnChangePassword.isEnabled = false
            }
        }


        binding.btnChangePassword.setOnClickListener {
            val password = binding.edtNewPassword.text.toString()
            val confirmPassword = binding.edtConfirmNewPassword.text.toString()
            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    // function to change password
                    viewModel.resetPassword(
                        email,
                        otp,
                        password
                    ).observe(this) { result ->
                        when (result) {
                            is Result.Loading -> {
                                // Show loading
                            }
                            is Result.Error -> {
                                showError(result.error!!)
                            }
                            is Result.Success -> {
                                startActivity(Intent(this, LoginActivity::class.java))
                                Toast.makeText(this, "Password has been changed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    binding.edtConfirmNewPassword.error = "Password is not match"
                }
            } else {
                binding.edtNewPassword.error = "Password is required"
                binding.edtConfirmNewPassword.error = "Password is required"
            }
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.error_title))
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            .show()
    }
}