package com.example.pathxplorer.ui.utils

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible

object AnimationUtils {
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.isVisible = true
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun fadeOut(view: View, duration: Long = 300, onEnd: () -> Unit = {}) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.isVisible = false
                onEnd()
            }
            .start()
    }

    fun slideUp(view: View, duration: Long = 300) {
        view.translationY = 50f
        view.alpha = 0f
        view.isVisible = true
        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun slideDown(view: View, duration: Long = 300, onEnd: () -> Unit = {}) {
        view.animate()
            .translationY(50f)
            .alpha(0f)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                view.isVisible = false
                onEnd()
            }
            .start()
    }

    fun buttonPress(view: View) {
        view.animate()
            .scaleX(0.95f)
            .scaleY(0.95f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    fun listItemEnter(view: View, position: Int, duration: Long = 300) {
        view.alpha = 0f
        view.translationY = 50f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(duration)
            .setStartDelay(position * 50L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    fun pulseAnimation(view: View, duration: Long = 1000) {
        ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f, 1f).apply {
            this.duration = duration
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
        ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f, 1f).apply {
            this.duration = duration
            repeatCount = ObjectAnimator.INFINITE
            start()
        }
    }

    fun staggeredFadeIn(views: List<View>, duration: Long = 300, delay: Long = 100) {
        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.isVisible = true
            view.animate()
                .alpha(1f)
                .setDuration(duration)
                .setStartDelay(index * delay)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }
    }
    fun animateCard(view: View, position: Int, duration: Long = 300) {
        view.alpha = 0f
        view.translationX = -100f
        view.animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(duration)
            .setStartDelay(position * 100L)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}