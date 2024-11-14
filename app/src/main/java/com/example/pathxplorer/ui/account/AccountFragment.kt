package com.example.pathxplorer.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.pathxplorer.MainViewModel
import com.example.pathxplorer.R
import com.example.pathxplorer.UserViewModelFactory
import com.example.pathxplorer.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
               binding.tvUserName.text = user.name
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}