package android.waterreminder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class WaterReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "water_reminder_channel"

        // 1. Create a Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to drink water regularly"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Build the visual notification banner
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: System default icon for now
            .setContentTitle("Time to Hydrate! 💧")
            .setContentText("Keep your energy up. Take a quick break and drink a glass of water.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismisses the notification when clicked
            .build()

        // 3. Fire the notification (ID 1 acts as a unique reference slot)
        notificationManager.notify(1, notification)
    }
}