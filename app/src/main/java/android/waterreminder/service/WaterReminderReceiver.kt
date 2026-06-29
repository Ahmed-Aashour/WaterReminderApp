package android.waterreminder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.waterreminder.R
import android.waterreminder.data.store.AppSettingsDataStore
import android.waterreminder.service.usecase.ResolveTrackingWindowUseCase
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint // Critical: Enables field injection inside this BroadcastReceiver
class WaterReminderReceiver : BroadcastReceiver() {

    @Inject lateinit var appSettingsDataStore: AppSettingsDataStore
    @Inject lateinit var resolveTrackingWindowUseCase: ResolveTrackingWindowUseCase

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val appContext = context.applicationContext
        val scheduler = WaterNotificationScheduler(appContext)

        scope.launch {
            val prefs = appSettingsDataStore.settingsFlow.first()

            if (!prefs.areNotificationsEnabled) {
                scheduler.cancelReminders()
                return@launch
            }

            val window = resolveTrackingWindowUseCase.execute(prefs)

            scheduler.scheduleNextReminder(
                startTime = window.first,
                endTime = window.second,
                intervalMinutes = prefs.frequency
            )

            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "water_reminder_channel"

            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water regularly"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)

            // 1. INTENT FOR QUICK DRINK (+250ml) BUTTON
            val quickDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_QUICK_DRINK
            }
            val quickDrinkPendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                quickDrinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 2. INTENT & INPUT FOR CUSTOM TEXT REPLY FIELD
            val remoteInput = RemoteInput.Builder(NotificationActionReceiver.KEY_CUSTOM_WATER_AMOUNT).apply {
                setLabel("Amount in ml (e.g., 350)")
            }.build()

            val customDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.ACTION_CUSTOM_DRINK
            }
            // Must use FLAG_MUTABLE for RemoteInput text input to be attached by the system!
            val customDrinkPendingIntent = PendingIntent.getBroadcast(
                context,
                102,
                customDrinkIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            // 3. BUILD THE INTERACTIVE BANNER
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Time to Hydrate your body! 💧")
                .setContentText("Go drink glass of water and enter the amount")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .addAction(
                    android.R.drawable.ic_menu_add,
                    "+250 ml",
                    quickDrinkPendingIntent
                )
                .addAction(
                    NotificationCompat.Action.Builder(
                        android.R.drawable.ic_menu_edit,
                        "Custom Amount",
                        customDrinkPendingIntent
                    ).addRemoteInput(remoteInput).build()
                )
                .build()

            notificationManager.notify(1, notification)
        }
    }
}
