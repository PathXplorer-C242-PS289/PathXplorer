package com.example.pathxplorer.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.pathxplorer.ui.utils.UserViewModelFactory
import com.example.pathxplorer.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnLogout.setOnClickListener {
            viewModel.getSession().observe(viewLifecycleOwner) { user ->
                if (user.provider !== "credential") {
                    signOutGoogle()
//                    startActivity(Intent(requireActivity(), SplashActivity::class.java))
                } else {
                    viewModel.logout()
//                    startActivity(Intent(requireActivity(), SplashActivity::class.java))
                }
            }
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                binding.tvUserName.text = user.name
            }
        }

    }

    private fun signOutGoogle() {
        lifecycleScope.launch {
            val credentialManager = CredentialManager.create(requireActivity())
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            viewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}