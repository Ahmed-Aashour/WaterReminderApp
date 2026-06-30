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
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HydrationReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var appSettingsDataStore: AppSettingsDataStore
    @Inject lateinit var resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Alarm triggered! Processing hydration background window...")

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        val appContext = context.applicationContext
        val scheduler = WaterNotificationScheduler(appContext)

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

                // 1. Quick Add Intent Setup (+250ml)
                val quickDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = NotificationActionReceiver.ACTION_QUICK_DRINK
                }
                val quickDrinkPendingIntent = PendingIntent.getBroadcast(
                    context,
                    101,
                    quickDrinkIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 2. Custom Text Field Input Setup via RemoteInput
                val remoteInput = RemoteInput.Builder(NotificationActionReceiver.KEY_CUSTOM_WATER_AMOUNT).apply {
                    setLabel(appContext.getString(R.string.notification_remote_input_label))
                }.build()

                val customDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                    action = NotificationActionReceiver.ACTION_CUSTOM_DRINK
                }
                val customDrinkPendingIntent = PendingIntent.getBroadcast(
                    context,
                    102,
                    customDrinkIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE // Must stay mutable for inline keyboard input
                )

                // 3. Assemble Localized Interactive Notification
                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(appContext.getString(R.string.notification_reminder_title))
                    .setContentText(appContext.getString(R.string.notification_reminder_text))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .addAction(
                        android.R.drawable.ic_menu_add,
                        appContext.getString(R.string.notification_action_quick_add),
                        quickDrinkPendingIntent
                    )
                    .addAction(
                        NotificationCompat.Action.Builder(
                            android.R.drawable.ic_menu_edit,
                            appContext.getString(R.string.notification_action_custom),
                            customDrinkPendingIntent
                        ).addRemoteInput(remoteInput).build()
                    )
                    .build()

                notificationManager.notify(NOTIFICATION_ID, notification)
                Log.d(TAG, "Hydration reminder notification dispatched successfully.")

            } catch (e: Exception) {
                Log.e(TAG, "Failed dispatching push reminder asset sequence: ${e.message}", e)
            } finally {
                pendingResult.finish() // Relinquish process priority hold back to Android OS
                scope.cancel()
            }
        }
    }

    companion object {
        private const val TAG = "HydrationReminderReceiver"
        private const val NOTIFICATION_ID = 1
    }
}