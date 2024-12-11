package com.example.pathxplorer.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.pathxplorer.R
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.ActivityDetailsWebinarBinding
import com.example.pathxplorer.ui.utils.AnimationUtils

class DetailWebinarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsWebinarBinding

    private val viewModel: WebinarViewModel by viewModels {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsWebinarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views as invisible
        binding.apply {
            contentContainer.alpha = 0f
            loadingIndicator.isVisible = false
            buttonOpenLink.alpha = 0f
        }

        setupToolbar()
        setupObservers()
        setupClickListeners()
        fetchEventDetail()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                animateExit { finish() }
            }
            title = getString(R.string.webinar_details)
        }
        AnimationUtils.slideUp(binding.toolbar)
    }

    private fun setupObservers() {
        viewModel.eventDetailLiveData.observe(this) { state ->
            when (state) {
                is WebinarState.Loading -> {
                    AnimationUtils.fadeIn(binding.loadingIndicator)
                    binding.contentContainer.isVisible = false
                }
                is WebinarState.Success -> {
                    AnimationUtils.fadeOut(binding.loadingIndicator)
                    binding.contentContainer.isVisible = true
                    displayEventDetails(state.data)
                }
                is WebinarState.Error -> {
                    AnimationUtils.fadeOut(binding.loadingIndicator)
                    showToast(state.message)
                }
            }
        }
    }

    private fun displayEventDetails(event: WebinarModel) {
        binding.apply {
            // Animate content container
            contentContainer.alpha = 0f
            contentContainer.translationY = 50f

            contentContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            // Load image with transition
            Glide.with(this@DetailWebinarActivity)
                .load(event.imageLogo)
                .placeholder(R.drawable.webinar)
                .error(R.drawable.webinar)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(detailEventImage)

            // Animate text elements sequentially
            val animationDelay = 100L
            animateTextView(detailEventName, event.name, 0 * animationDelay)
            animateTextView(detailEventOwner, event.ownerName, 1 * animationDelay)
            animateTextView(detailEventTime, event.beginTime, 2 * animationDelay)
            animateTextView(detailEventQuota, getString(
                R.string.quota_format,
                (event.quota ?: 0) - (event.registrants ?: 0)
            ), 3 * animationDelay)

            detailEventDescription.apply {
                text = Html.fromHtml(event.description ?: "", Html.FROM_HTML_MODE_COMPACT)
                movementMethod = LinkMovementMethod.getInstance()
                animateTextView(this, null, 4 * animationDelay)
            }

            // Animate button
            buttonOpenLink.alpha = 0f
            buttonOpenLink.translationY = 50f
            buttonOpenLink.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(5 * animationDelay)
                .setDuration(300)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }

    private fun animateTextView(view: View, newText: String? = null, delay: Long = 0) {
        view.alpha = 0f
        view.translationX = -50f

        if (newText != null && view is TextView) {
            view.text = newText
        }

        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setStartDelay(delay)
            .setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    private fun setupClickListeners() {
        binding.buttonOpenLink.setOnClickListener {
            AnimationUtils.buttonPress(it)
            when (val state = viewModel.eventDetailLiveData.value) {
                is WebinarState.Success -> {
                    state.data.link?.let { link ->
                        openWebLink(link)
                    } ?: showToast(getString(R.string.event_link_not_available))
                }
                else -> showToast(getString(R.string.event_link_not_available))
            }
        }
    }

    private fun fetchEventDetail() {
        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)
        viewModel.fetchEventDetail(eventId)
    }

    private fun openWebLink(link: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        } catch (e: Exception) {
            showToast(getString(R.string.unable_to_open_link))
        }
    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.view?.alpha = 0f
        toast.view?.animate()
            ?.alpha(1f)
            ?.setDuration(200)
            ?.withEndAction { toast.show() }
            ?.start()
    }

    private fun animateExit(onComplete: () -> Unit) {
        binding.apply {
            val duration = 300L

            toolbar.animate()
                .alpha(0f)
                .translationY(-toolbar.height.toFloat())
                .setDuration(duration)
                .start()

            contentContainer.animate()
                .alpha(0f)
                .translationY(50f)
                .setDuration(duration)
                .withEndAction(onComplete)
                .start()

            buttonOpenLink.animate()
                .alpha(0f)
                .translationY(50f)
                .setDuration(duration)
                .start()
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "event_id"

        fun createIntent(context: Context, eventId: Int): Intent {
            return Intent(context, DetailWebinarActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, eventId)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}