package com.example.pathxplorer.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.pathxplorer.MainActivity
import com.example.pathxplorer.R
import com.example.pathxplorer.data.models.OnboardingItem
import com.example.pathxplorer.databinding.ActivitySplashBinding
import com.example.pathxplorer.ui.auth.LoginActivity
import com.example.pathxplorer.ui.main.adapter.SplashAdapter
import com.example.pathxplorer.ui.utils.UserViewModelFactory

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var onboardingAdapter: SplashAdapter

    private val viewModel by viewModels<MainViewModel> {
        UserViewModelFactory.getInstance(this)
    }

    companion object {
        private const val PREFS_NAME = "PathXplorerPrefs"
        private const val FIRST_TIME_LAUNCH = "isFirstTimeLaunch"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isFirstTimeLaunch = sharedPreferences.getBoolean(FIRST_TIME_LAUNCH, true)

        if (!isFirstTimeLaunch) {
            proceedToNextScreen()
            return
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupOnboardingItems()
        setupIndicators()
        setCurrentIndicator(0)

        binding.SplashViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
                updateButtonsVisibility(position)
            }
        })

        binding.buttonNext.setOnClickListener {
            if (binding.SplashViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                binding.SplashViewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }

        binding.buttonSkip.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun proceedToNextScreen() {
        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }

    private fun completeOnboarding() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(FIRST_TIME_LAUNCH, false)
            .apply()

        proceedToNextScreen()
    }

    private fun updateButtonsVisibility(position: Int) {
        if (position == onboardingAdapter.itemCount - 1) {
            binding.buttonNext.text = getString(R.string.get_started)
            binding.buttonSkip.alpha = 0f
        } else {
            binding.buttonNext.text = getString(R.string.next)
            binding.buttonSkip.alpha = 1f
        }
    }

    private fun setupOnboardingItems() {
        val onboardingItems = listOf(
            OnboardingItem(
                title = "Choose Your Path",
                description = "Amid many options, discover the path that aligns with your true interests and guides you toward your future.",
                image = R.drawable.splash_img1
            ),
            OnboardingItem(
                title = "Find Your True Path",
                description = "Find a path that suits your interests and potential, and make the right decisions for a brighter future.",
                image = R.drawable.splash_img2
            ),
            OnboardingItem(
                title = "Step Confidence",
                description = "Follow the path that aligns with your goals, and move forward with certainty towards your dreams.",
                image = R.drawable.splash_img3
            )
        )

        onboardingAdapter = SplashAdapter(onboardingItems)
        binding.SplashViewPager.adapter = onboardingAdapter
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this.layoutParams = layoutParams
            }
            binding.indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = binding.indicatorsContainer.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}