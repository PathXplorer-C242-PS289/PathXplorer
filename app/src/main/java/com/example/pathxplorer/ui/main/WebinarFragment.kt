package com.example.pathxplorer.ui.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.databinding.FragmentWebinarBinding
import com.example.pathxplorer.ui.main.adapter.WebinarAdapter
import com.example.pathxplorer.ui.utils.AnimationUtils

class WebinarFragment : Fragment() {
    private var _binding: FragmentWebinarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WebinarViewModel by viewModels {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    private lateinit var webinarAdapter: WebinarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebinarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeWebinars()
        viewModel.fetchWebinars()

        binding.rvWebinars.alpha = 0f
        binding.rvWebinars.translationY = 100f
    }

    private fun setupRecyclerView() {
        webinarAdapter = WebinarAdapter(
            onItemClick = { webinar, itemView ->
                webinar.id?.let { id ->
                    AnimationUtils.buttonPress(itemView)
                    startActivity(DetailWebinarActivity.createIntent(requireContext(), id))
                }
            }
        )

        binding.rvWebinars.apply {
            adapter = webinarAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    animateItemsOnScroll(recyclerView)
                }
            })
        }
    }

    private fun animateItemsOnScroll(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        for (i in firstVisible..lastVisible) {
            val view = layoutManager.findViewByPosition(i) ?: continue
            if (view.getTag(R.id.view_animation_tag) != true) {
                animateItem(view)
                view.setTag(R.id.view_animation_tag, true)
            }
        }
    }

    private fun animateItem(view: View) {
        view.alpha = 0f
        view.translationX = 100f

        val alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val translateAnimator = ObjectAnimator.ofFloat(view, "translationX", 100f, 0f)

        AnimatorSet().apply {
            playTogether(alphaAnimator, translateAnimator)
            duration = 300
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun observeWebinars() {
        viewModel.webinars.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    showLoading(false)
                    webinarAdapter.submitList(result.data)
                    animateContent()
                }
                is Result.Error -> {
                    showLoading(false)
                    showError(result.error.toString())
                }
                is Result.Loading -> showLoading(true)
            }
        }
    }

    private fun animateContent() {
        binding.rvWebinars.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            AnimationUtils.fadeIn(binding.progressBar)
        } else {
            AnimationUtils.fadeOut(binding.progressBar)
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}