package com.example.pathxplorer.ui.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.pathxplorer.R
import com.example.pathxplorer.data.Result
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.data.remote.response.ProfileWithTestResponse
import com.example.pathxplorer.data.remote.retrofit.ApiConfig
import com.example.pathxplorer.ui.main.ProfileSettingsActivity
import com.example.pathxplorer.ui.quiz.test.QuizActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import androidx.lifecycle.asFlow
import com.example.pathxplorer.data.local.room.DailyDatabase

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

        // Show loading initially
        views.setViewVisibility(R.id.widget_content, View.GONE)
        views.setViewVisibility(R.id.widget_loading, View.VISIBLE)
        appWidgetManager.updateAppWidget(appWidgetId, views)

        // Setup click listeners
        setupClickListeners(context, views, appWidgetId)

        scope.launch {
            var latestRiasecType = "No recent test"
            var levelText = "-"
            try {
                val userPreference = UserPreference.getInstance(context.dataStore)
                val user = userPreference.getSession().first()

                Log.d("UserProfileWidget", "User: ${user.name}, Token: ${user.token}")

                val apiService = ApiConfig.getApiService(user.token)
                val dailyDao = DailyDatabase.getInstance(context).dailyDao()
                val userRepository = UserRepository.getInstance(apiService, dailyDao, userPreference)

                val dailyQuestLiveData = userRepository.getDailyQuestById(user.userId)

                val testResultsLiveData = userRepository.getTestResults()
                val result = testResultsLiveData.asFlow().first()

                if (result is Result.Success<ProfileWithTestResponse>) {
                    val tests = result.data.data.testResults
                    Log.d("UserProfileWidget", "Tests received: ${tests.size}")
                    tests.forEach { test ->
                        Log.d("UserProfileWidget", "TestID: ${test.testId}, Riasec: ${test.riasecType}, Timestamp: ${test.timestamp}")
                    }
                    withContext(Dispatchers.Main) {
                        views.apply {
                            setTextViewText(R.id.widget_tests_label, "Tests")
                            setTextViewText(R.id.widget_tests, result.data.data.testResults.size.toString())
                        }
                    }

                    // The logs show newest test is the first one returned, so take tests.first()
                    if (tests.isNotEmpty()) {
                        val latestTest = tests.first()
                        latestRiasecType = latestTest.riasecType ?: "No recent test"
                        Log.d("UserProfileWidget", "Latest RIASEC Type: $latestRiasecType")
                    } else {
                        Log.d("UserProfileWidget", "No tests available")
                    }
                } else {
                    Log.d("UserProfileWidget", "Result not success or no tests found")
                }

                val level = when (dailyDao.getDailyQuest(user.userId).score) {
                    null, 0 -> "Pemula"
                    in 1..30 -> "Junior"
                    in 31..60 -> "Intermediate"
                    in 61..90 -> "Advanced"
                    else -> "Expert"
                }
                levelText = level

                withContext(Dispatchers.Main) {
                    views.apply {
                        setTextViewText(R.id.widget_name, user.name)
                        setTextViewText(R.id.widget_email, user.email)

                        setTextViewText(R.id.widget_level_label, "Level")
                        setTextViewText(R.id.widget_level, levelText)

                        setTextViewText(R.id.widget_daily_quest_label, "Daily Quest")
                        setTextViewText(R.id.widget_daily_quest, dailyQuestLiveData.dailyQuestCount.toString())

                        setTextViewText(R.id.widget_score_label, "Score")
                        setTextViewText(R.id.widget_score, dailyQuestLiveData.score.toString())

                        setTextViewText(R.id.widget_latest_test, "Latest RIASEC Type: $latestRiasecType")

                        setViewVisibility(R.id.widget_loading, View.GONE)
                        setViewVisibility(R.id.widget_content, View.VISIBLE)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                Log.e("UserProfileWidget", "Error loading widget data", e)
                withContext(Dispatchers.Main) {
                    views.apply {
                        setTextViewText(R.id.widget_latest_test, "No recent test")
                        setViewVisibility(R.id.widget_loading, View.GONE)
                        setViewVisibility(R.id.widget_content, View.VISIBLE)
                    }
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            }
        }
    }

    private fun setupClickListeners(context: Context, views: RemoteViews, appWidgetId: Int) {
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
