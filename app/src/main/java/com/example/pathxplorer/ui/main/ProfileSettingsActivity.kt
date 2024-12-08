package com.example.pathxplorer.ui.main

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.databinding.ActivityProfileSettingsBinding
import com.example.pathxplorer.ui.utils.UserProfileWidget
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: MainViewModel by viewModels {
        UserViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
            title = "Profile Settings"
        }
    }

    private fun loadUserData() {
        viewModel.getSession().observe(this) { user ->
            binding.apply {
                edtName.setText(user.name)
                edtEmail.setText(user.email)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnChangePhoto.setOnClickListener {
                showImagePicker()
            }

            btnSave.setOnClickListener {
                saveChanges()
            }
        }
    }

    private fun showImagePicker() {
        Toast.makeText(this, "Image picker coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun saveChanges() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()

        if (validateInput(name, email)) {
            binding.btnSave.isEnabled = false

            auth.currentUser?.let { user ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()

                user.updateProfile(profileUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            viewModel.getSession().observe(this) { currentUser ->
                                viewModel.saveSession(
                                    UserModel(
                                        email = email,
                                        name = name,
                                        token = user.getIdToken(false).result?.token ?: "",
                                        userId = user.uid.toInt(),
                                        provider = currentUser.provider,
                                        isLogin = true,
                                        testCount = currentUser.testCount,
                                        dailyQuestCount = currentUser.dailyQuestCount,
                                        score = currentUser.score
                                    )
                                )
                                updateWidget()
                                showSuccessDialog()
                            }
                        } else {
                            showError("Profile update failed. Please try again.")
                        }
                        binding.btnSave.isEnabled = true
                    }
            }
        }
    }

    private fun updateWidget() {
        try {
            val intent = Intent(this, UserProfileWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(application)
                .getAppWidgetIds(ComponentName(application, UserProfileWidget::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            sendBroadcast(intent)
        } catch (e: Exception) {
            // Handle widget update failure silently
        }
    }

    private fun validateInput(name: String, email: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        return isValid
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Success")
            .setMessage("Profile updated successfully")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}