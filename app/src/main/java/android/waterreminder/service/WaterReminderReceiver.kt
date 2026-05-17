package android.waterreminder.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput

class WaterReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water regularly"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Trigger the next alarm for repeating notifications
        val scheduler = WaterNotificationScheduler(context.applicationContext)
        scheduler.scheduleRepeatingReminders()

        // 1. INTENT FOR QUICK DRINK (+250ml) BUTTON
        val quickDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_QUICK_DRINK"
        }
        val quickDrinkPendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            quickDrinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. INTENT & INPUT FOR CUSTOM TEXT REPLY FIELD
        val remoteInput = RemoteInput.Builder("KEY_CUSTOM_WATER_AMOUNT").apply {
            setLabel("Amount in ml (e.g., 350)")
        }.build()

        val customDrinkIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_CUSTOM_DRINK"
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
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to Hydrate your body! 💧")
            .setContentText("Go drink water and enter the amount you drank")
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