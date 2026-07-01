package android.waterreminder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.waterreminder.R
import android.waterreminder.data.database.HydrationDatabase
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Intercepts periodic [android.app.AlarmManager] signals to process hydration alert intervals.
 *
 * When triggered, this receiver handles three core background responsibilities:
 * 1. Automatically re-arms and schedules the next downstream reminder step via [HydrationReminderScheduler].
 * 2. Queries active logging presets directly from the Room database.
 * 3. Builds and dispatches a high-priority system notification containing interactive logging buttons.
 */
@AndroidEntryPoint
class HydrationReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var appSettingsDataStore: AppSettingsDataStore
    @Inject lateinit var resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase

    /**
     * Executes localized sequence building when a background tracking alarm fires.
     *
     * Spawns an isolated I/O thread context under a [goAsync] execution contract to safely complete database
     * transactions and layout assembly routines before relinquishing process priority back to the OS.
     *
     * @param context The application or system execution context environment.
     * @param intent The trigger details delivered by the system scheduler.
     */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm triggered! Processing hydration background window...")

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val appContext = context.applicationContext
        val scheduler = HydrationReminderScheduler(appContext)

        scope.launch {
            try {
                val prefs = appSettingsDataStore.settingsFlow.first()

                // Guard Clause: If notifications are flipped off, clear alarms and bail
                if (!prefs.areNotificationsEnabled) {
                    Log.d(TAG, "Notifications are globally disabled. Canceling future intervals.")
                    scheduler.cancelReminders()
                    return@launch
                }

                // Reschedule next implicit cycle alarm sequence
                val window = resolveTrackingWindowUseCase.execute(prefs)
                scheduler.scheduleNextReminder(
                    startTime = window.first,
                    endTime = window.second,
                    intervalMinutes = prefs.frequency
                )
                Log.d(TAG, "Next reminder interval successfully queued.")

                // Setup system notification service
                val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "water_reminder_channel"

                // Create channel safely using local system resource strings
                val channel = NotificationChannel(
                    channelId,
                    appContext.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = appContext.getString(R.string.notification_channel_description)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager.createNotificationChannel(channel)

                // Fetch current catalog presets directly from the database source of truth
                val database = HydrationDatabase.getDatabase(appContext, this)
                val catalogCups = database.dashboardDao().getCupsCatalogFlow().first()

                // Fetch current total intake from database source of truth
                val startOfDayTimestamp = java.time.LocalDate.now()
                    .atStartOfDay(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val totalIntakeMl = database.dashboardDao().getTodayTotalIntakeFlow(startOfDayTimestamp).first()

                val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(appContext.getString(R.string.notification_reminder_title))
                    .setContentText(appContext.getString(R.string.notification_reminder_text))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(false) // Let the user swipe it away manually when done tracking!

                // Inject dynamic progress data
                updateProgressContent(notificationBuilder, appContext, totalIntakeMl, prefs.dailyGoalMl, prefs.unit)

                // Dynamically generate notification actions based on active catalog items.
                // Enforces a strict maximum layout ceiling of 3 actions to preserve native system layouts.
                catalogCups.take(3).forEachIndexed { index, cup ->
                    val amountMl = cup.amountMl

                    // Format the button text dynamically according to preferred user units (ml / fl oz)
                    val convertedAmount = prefs.unit.convertFromMl(amountMl)
                    val label = appContext.getString(prefs.unit.formatRes, convertedAmount)

                    val drinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                        action = NotificationActionReceiver.ACTION_QUICK_DRINK
                        putExtra(NotificationActionReceiver.EXTRA_WATER_AMOUNT, amountMl)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        index + 200, // Unique request code per preset button
                        drinkIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    notificationBuilder.addAction(
                        android.R.drawable.ic_menu_add,
                        label,
                        pendingIntent
                    )
                }

                notificationManager.notify(
                    NotificationActionReceiver.NOTIFICATION_ID,
                    notificationBuilder.build()
                )
                Log.d(TAG, "Hydration reminder notification dispatched successfully.")

            } catch (e: Exception) {
                Log.e(TAG, "Failed dispatching push reminder asset sequence: ${e.message}", e)
            } finally {
                pendingResult.finish() // Relinquish process priority hold back to Android OS
                scope.cancel()
            }
        }
    }

    private fun updateProgressContent(
        builder: NotificationCompat.Builder,
        context: Context,
        totalIntakeMl: Int,
        dailyGoalMl: Int,
        unit: AppUnit
    ) {
        val convertedIntake = unit.convertFromMl(totalIntakeMl)
        val convertedGoal = unit.convertFromMl(dailyGoalMl)

        // Example format: "Progress: 1200 / 2000 ml" or "Progress: 40 / 67 fl oz"
        val progressText = "${context.getString(R.string.notification_progress_prefix)}: " +
                "$convertedIntake / $convertedGoal ${context.getString(unit.unitRes)}"

        builder.setContentText(progressText)

        // Optional: Add a real native progress bar inside the notification deck!
        builder.setProgress(dailyGoalMl, totalIntakeMl, false)
    }

    companion object {
        private const val TAG = "HydrationReminderReceiver"
    }
}