package com.example.pathxplorer.ui.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.pathxplorer.R
import com.example.pathxplorer.data.WebinarRepository
import com.example.pathxplorer.data.models.WebinarModel
import com.example.pathxplorer.databinding.ActivityDetailsWebinarBinding

class DetailWebinarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsWebinarBinding

    private val viewModel: WebinarViewModel by viewModels {
        WebinarViewModelFactory(WebinarRepository.getInstance())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsWebinarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupObservers()
        setupClickListeners()
        fetchEventDetail()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationOnClickListener { finish() }
            title = getString(R.string.webinar_details)
        }
    }

    private fun setupObservers() {
        viewModel.eventDetailLiveData.observe(this) { state ->
            when (state) {
                is WebinarState.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.contentContainer.visibility = View.GONE
                }
                is WebinarState.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.contentContainer.visibility = View.VISIBLE
                    displayEventDetails(state.data)
                }
                is WebinarState.Error -> {
                    binding.loadingIndicator.visibility = View.GONE
                    showToast(state.message)
                }
            }
        }
    }

    private fun displayEventDetails(event: WebinarModel) {
        binding.apply {
            Glide.with(this@DetailWebinarActivity)
                .load(event.imageLogo)
                .placeholder(R.drawable.webinar)
                .error(R.drawable.webinar)
                .into(detailEventImage)

            detailEventName.text = event.name
            detailEventOwner.text = event.ownerName
            detailEventTime.text = event.beginTime
            detailEventQuota.text = getString(
                R.string.quota_format,
                (event.quota ?: 0) - (event.registrants ?: 0)
            )

            detailEventDescription.apply {
                text = Html.fromHtml(event.description ?: "", Html.FROM_HTML_MODE_COMPACT)
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }

    private fun setupClickListeners() {
        binding.buttonOpenLink.setOnClickListener {
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
        } catch (e: Exception) {
            showToast(getString(R.string.unable_to_open_link))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_EVENT_ID = "event_id"

        fun createIntent(context: Context, eventId: Int): Intent {
            return Intent(context, DetailWebinarActivity::class.java).apply {
                putExtra(EXTRA_EVENT_ID, eventId)
            }
        }
    }
}