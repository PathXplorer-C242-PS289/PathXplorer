package com.example.pathxplorer.ui.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.example.pathxplorer.R
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.ui.main.ProfileSettingsActivity
import com.example.pathxplorer.ui.quiz.test.QuizActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserProfileWidget : AppWidgetProvider() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_utils)

        // Set initial loading state
        views.setViewVisibility(R.id.widget_content, View.GONE)
        views.setViewVisibility(R.id.widget_loading, View.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Setup click listeners
        setupClickListeners(context, views, appWidgetId)

        scope.launch {
            try {
                val userPreference = UserPreference.getInstance(context.dataStore)
                val user = userPreference.getSession().first()

                val level = when (user.score) {
                    null, 0 -> "Pemula"
                    in 1..30 -> "Junior"
                    in 31..60 -> "Intermediate"
                    in 61..90 -> "Advanced"
                    else -> "Expert"
                }

                views.apply {
                    // Update user info
                    setTextViewText(R.id.widget_name, user.name)
                    setTextViewText(R.id.widget_email, user.email)

                    // Update stats
                    setTextViewText(R.id.widget_level_label, "Level")
                    setTextViewText(R.id.widget_level, level)

                    setTextViewText(R.id.widget_tests_label, "Tests")
                    setTextViewText(R.id.widget_tests, user.testCount?.toString() ?: "0")

                    setTextViewText(R.id.widget_daily_quest_label, "Daily Quest")
                    setTextViewText(R.id.widget_daily_quest, user.dailyQuestCount?.toString() ?: "0")

                    setTextViewText(R.id.widget_score_label, "Score")
                    setTextViewText(R.id.widget_score, user.score?.toString() ?: "0")

                    setViewVisibility(R.id.widget_loading, View.GONE)
                    setViewVisibility(R.id.widget_content, View.VISIBLE)
                }

                appWidgetManager.updateAppWidget(appWidgetId, views)
            } catch (e: Exception) {
                views.apply {
                    setViewVisibility(R.id.widget_loading, View.GONE)
                    setViewVisibility(R.id.widget_content, View.VISIBLE)
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun setupClickListeners(context: Context, views: RemoteViews, appWidgetId: Int) {
        // Profile edit click
        val editIntent = Intent(context, ProfileSettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val editPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            editIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_edit, editPendingIntent)

        // Refresh click
        val refreshIntent = Intent(context, UserProfileWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId + 100,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_refresh, refreshPendingIntent)

        // Take Test button click
        val testIntent = Intent(context, QuizActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val testPendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId + 200,
            testIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_take_test_button, testPendingIntent)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        scope.cancel()
    }
}