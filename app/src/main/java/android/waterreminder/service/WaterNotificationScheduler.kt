package android.waterreminder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.waterreminder.service.utils.TimeUtils

class WaterNotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextReminder(startTime: String, endTime: String, intervalMinutes: Int) {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 🌟 Compute the precise trigger timestamp using our operational window logic
        // TODO: Move the function definition here
        val triggerTimeMs = TimeUtils.calculateNextTriggerMillis(
            startTimeStr = startTime,
            endTimeStr = endTime,
            intervalMinutes = intervalMinutes
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
            }
            Log.d("WaterScheduler", "Next reminder scheduled successfully for epoch timestamp: $triggerTimeMs")
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeMs, pendingIntent)
        }
    }

    fun cancelReminders() {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("WaterScheduler", "Hydration reminders canceled.")
    }
}