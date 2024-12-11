package com.example.pathxplorer.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.databinding.ActivityProfileSettingsBinding
import com.example.pathxplorer.ui.utils.AnimationUtils
import com.example.pathxplorer.ui.utils.UserProfileWidget
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

        binding.apply {
            contentLayout.alpha = 0f
            loadingIndicator.isVisible = false
            btnChangePhoto.alpha = 0f
            btnSave.alpha = 0f
        }

        auth = FirebaseAuth.getInstance()
        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.apply {
            setNavigationOnClickListener {
                animateExit { finish() }
            }
            title = "Profile Settings"
        }
        AnimationUtils.slideUp(binding.toolbar)
    }

    private fun loadUserData() {
        AnimationUtils.fadeIn(binding.loadingIndicator)

        viewModel.getSession().observe(this) { user ->
            AnimationUtils.fadeOut(binding.loadingIndicator)
            animateContent(user)
        }
    }

    private fun animateContent(user: UserModel) {
        binding.apply {
            contentLayout.alpha = 0f
            contentLayout.translationY = 50f
            contentLayout.isVisible = true

            contentLayout.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(DecelerateInterpolator())
                .start()

            // Set user data
            edtName.setText(user.name)
            edtEmail.setText(user.email)

            // Animate form fields
            val animatorSet = AnimatorSet()
            val nameAnim = ObjectAnimator.ofFloat(tilName, "translationX", -50f, 0f)
            val emailAnim = ObjectAnimator.ofFloat(tilEmail, "translationX", -50f, 0f)

            nameAnim.duration = 400
            emailAnim.duration = 400

            animatorSet.playSequentially(nameAnim, emailAnim)
            animatorSet.start()

            // Animate buttons
            AnimationUtils.fadeIn(btnChangePhoto, 300)
            AnimationUtils.fadeIn(btnSave, 400)
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnChangePhoto.setOnClickListener {
                AnimationUtils.buttonPress(it)
                showImagePicker()
            }

            btnSave.setOnClickListener {
                AnimationUtils.buttonPress(it)
                if (validateInput()) {
                    saveChanges()
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        var isValid = true

        binding.apply {
            if (name.isEmpty()) {
                tilName.error = "Name is required"
                AnimationUtils.buttonPress(tilName)
                isValid = false
            } else {
                tilName.error = null
            }

            if (email.isEmpty()) {
                tilEmail.error = "Email is required"
                AnimationUtils.buttonPress(tilEmail)
                isValid = false
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                tilEmail.error = "Invalid email format"
                AnimationUtils.buttonPress(tilEmail)
                isValid = false
            } else {
                tilEmail.error = null
            }
        }

        return isValid
    }

    private fun saveChanges() {
        binding.btnSave.isEnabled = false
        AnimationUtils.fadeIn(binding.loadingIndicator)

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()

        auth.currentUser?.let { user ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    AnimationUtils.fadeOut(binding.loadingIndicator)
                    if (task.isSuccessful) {
                        updateUserSession(user, name, email)
                    } else {
                        showError("Profile update failed. Please try again.")
                    }
                    binding.btnSave.isEnabled = true
                }
        }
    }

    private fun updateUserSession(user: FirebaseUser, name: String, email: String) {
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

    private fun showImagePicker() {
        Toast.makeText(this, "Image picker coming soon", Toast.LENGTH_SHORT).show()
    }

    private fun showSuccessDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Success")
            .setMessage("Profile updated successfully")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                animateExit { finish() }
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

    private fun animateExit(onComplete: () -> Unit) {
        binding.apply {
            AnimationUtils.fadeOut(btnSave)
            AnimationUtils.fadeOut(btnChangePhoto)
            AnimationUtils.slideDown(contentLayout, onEnd = onComplete)
        }
    }
}