package com.example.pathxplorer.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.MainActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.data.models.UserModel
import com.example.pathxplorer.databinding.ActivitySignupBinding
import com.example.pathxplorer.ui.utils.AuthViewModelFactory
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    private val viewModel by viewModels<AuthViewModel> {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        supportActionBar?.hide()
        setupAction()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val userName = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            isLoading(true)

            lifecycleScope.launch {
                viewModel.register(userName, email, password).observe(this@SignupActivity) { result ->
                    isLoading(false)
                    when (result) {
                        is Result.Loading -> {
                            isLoading(true)
                        }
                        is Result.Success -> {
                            val intent = Intent(this@SignupActivity, OTPVerificationActivity::class.java)
                            intent.putExtra(OTPVerificationActivity.EXTRA_EMAIL, email)
                            intent.putExtra(OTPVerificationActivity.TYPE_OTP, "RegisterAccount")
                            startActivity(intent)
                        }
                        is Result.Error -> {
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Oops!")
                                setMessage("${result.error}")
                                setPositiveButton("OK") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                create()
                                show()
                            }
                        }
                    }
                }
            }


        }

        binding.tvDaftar.setOnClickListener {
            finish()
        }

        binding.tvPrivacyPolicy.setOnClickListener {
            val dialog = AlertDialog.Builder(this).apply {
                setTitle("Privacy Policy")
                setPositiveButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }
                setMessage(R.string.privacy_policy_content)
            }
            dialog.create()
            dialog.show()
        }

        binding.btnSignupGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.google_client_id)) //from https://console.firebase.google.com/project/firebaseProjectName/authentication/providers
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential( //import from androidx.CredentialManager
                    request = request,
                    context = this@SignupActivity,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                Log.d("Error", e.message.toString())
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user: FirebaseUser? = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            val token = currentUser.getIdToken(false).result?.token ?: ""
            viewModel.registerWithGoogle(token).observe(this) { result ->
                Log.d(TAG, "updateUI: $token")
                when (result) {
                    is Result.Loading -> {
                        isLoading(true)
                    }
                    is Result.Error -> {
                        isLoading(false)
                        AlertDialog.Builder(this@SignupActivity).apply {
                            setTitle("Oops!")
                            setMessage(result.error)
                            setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Success -> {
                        Log.d(TAG, "updateUI: $result")
                        val intent = Intent(this@SignupActivity, MainActivity::class.java)
                        val user = UserModel(
                            email = result.data.user.email,
                            name = result.data.user.username,
                            token = result.data.token,
                            userId = result.data.user.id,
                            provider = "google",
                        )
                        viewModel.saveSession(user)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun isLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "SignupActivity"
    }
}