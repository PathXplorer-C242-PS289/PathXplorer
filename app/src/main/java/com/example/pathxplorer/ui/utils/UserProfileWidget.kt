package com.example.pathxplorer.ui.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.example.pathxplorer.R
import com.example.pathxplorer.data.UserRepository
import com.example.pathxplorer.data.local.datapreference.UserPreference
import com.example.pathxplorer.data.local.datapreference.dataStore
import com.example.pathxplorer.ui.main.ProfileSettingsActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UserProfileWidget : AppWidgetProvider() {

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

        val editIntent = Intent(context, ProfileSettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val editPendingIntent = PendingIntent.getActivity(
            context,
            0,
            editIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_edit, editPendingIntent)

        // Use coroutine to fetch user data
        CoroutineScope(Dispatchers.IO).launch {
            val userPreference = UserPreference.getInstance(context.dataStore)
            val userRepository = UserRepository.getInstance(userPreference)
            val user = userRepository.getSession().first()

            views.apply {
                setImageViewResource(R.id.widget_avatar, R.drawable.profile_icon)

                setTextViewText(R.id.widget_level, "Pemula")
                setTextViewText(R.id.widget_tests, user.testCount?.toString() ?: "0")
                setTextViewText(R.id.widget_daily_quest, user.dailyQuestCount?.toString() ?: "0")
                setTextViewText(R.id.widget_score, user.score?.toString() ?: "0")

                setInt(R.id.widget_background, "setBackgroundResource", R.drawable.widget_background)
                setTextColor(R.id.widget_level, ContextCompat.getColor(context, R.color.white))
                setTextColor(R.id.widget_tests, ContextCompat.getColor(context, R.color.white))
                setTextColor(R.id.widget_daily_quest, ContextCompat.getColor(context, R.color.white))
                setTextColor(R.id.widget_score, ContextCompat.getColor(context, R.color.white))
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

            if (appWidgetIds != null && context != null) {
                onUpdate(context, appWidgetManager, appWidgetIds)
            }
        }
    }
}