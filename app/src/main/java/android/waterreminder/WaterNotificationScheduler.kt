package android.waterreminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.util.Log

// Explicitly define the timing constant (1 hour in milliseconds)
const val REMINDER_INTERVAL_MS = 1 * 60 * 60 * 1000L

class WaterNotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Initializes the background repeating hydration alarms.
     */
    fun scheduleRepeatingReminders() {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val firstTriggerTime = System.currentTimeMillis() + REMINDER_INTERVAL_MS

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                firstTriggerTime,
                REMINDER_INTERVAL_MS,
                pendingIntent
            )
            Log.d("WaterScheduler", "Hydration reminders successfully initialized.")
        } catch (e: Exception) {
            Log.e("WaterScheduler", "Failed to schedule repeating alarm loop.", e)
        }
    }

    /**
     * Optional utility to completely cancel background alarms if the user turns reminders off
     */
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

    /**
     * Testing utility
     * */
    private fun scheduleTestReminder(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

        // Checking if the app is legally allowed to schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("WaterReminder", "Cannot schedule exact alarm: Permission denied by system/user.")
                // Fallback: Use an inexact alarm which doesn't require special permission
                scheduleInexactReminder(context, alarmManager)
                return
            }
        }

        // Alarm setup
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 10000

        // Wrap the scheduling call inside a try-catch block as a safety net
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } catch (e: SecurityException) {
            Log.e("WaterReminder", "SecurityException caught while scheduling alarm", e)
            scheduleInexactReminder(context, alarmManager)
        }
    }

    // Testing: Fallback method to prevent the engine from breaking entirely
    private fun scheduleInexactReminder(context: Context, alarmManager: AlarmManager) {
        val intent = Intent(context, WaterReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + 10000

        // setAndAllowWhileIdle lets Android shift the timing slightly to save battery, bypasses the restriction
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}