package android.waterreminder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class WaterNotificationScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextReminder(startTime: LocalTime, endTime: LocalTime, intervalMinutes: Int) {
        val intent = Intent(context, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTimeMs = calculateNextTriggerMillis(
            startTime = startTime,
            endTime = endTime,
            intervalMinutes = intervalMinutes
        )

        try {
            // 🚀 Production Fix: Check exact permission rules for API 31+ (Android 12) through API 34+ (Android 14) safely
            val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }

            if (canScheduleExact) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMs,
                    pendingIntent
                )
                Log.d(TAG, "Next exact reminder scheduled successfully for epoch timestamp: $triggerTimeMs")
            } else {
                // Safe background inexact scheduling fallback if the permission is missing
                scheduleInexactAlarm(triggerTimeMs, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException thrown trying to set exact alarm. Falling back to inexact.", e)
            scheduleInexactAlarm(triggerTimeMs, pendingIntent)
        }
    }

    fun cancelReminders() {
        val intent = Intent(context, HydrationReminderReceiver::class.java)

        // 🚀 Production Fix: Use FLAG_NO_CREATE to see if the alarm token actually exists first
        val existingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (existingIntent != null) {
            alarmManager.cancel(existingIntent)
            existingIntent.cancel() // Clear the system wrapper token
            Log.d(TAG, "Active hydration reminders found and canceled.")
        } else {
            Log.d(TAG, "No active reminder alarms were scheduled. Cancel skipped.")
        }
    }

    private fun scheduleInexactAlarm(triggerTimeMs: Long, pendingIntent: PendingIntent) {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMs,
            pendingIntent
        )
        Log.d(TAG, "Next inexact reminder scheduled successfully for epoch timestamp: $triggerTimeMs")
    }

    /**
     * Calculates the exact Epoch millisecond timestamp for
     * the next alarm trigger.
     */
    private fun calculateNextTriggerMillis(
        startTime: LocalTime,
        endTime: LocalTime,
        intervalMinutes: Int
    ): Long {
        val now = LocalDateTime.now()

        // Map today's explicit start and end parameters
        var startDateTime = now.with(startTime)
        var endDateTime = now.with(endTime)

        // Handle overnight shifts (e.g., Fasting Mode shifting from 6:45 PM to 4:15 AM tomorrow)
        if (endTime.isBefore(startTime)) {
            if (now.toLocalTime().isBefore(endTime)) {
                // We are currently in the post-midnight segment of the overnight window
                startDateTime = startDateTime.minusDays(1)
            } else {
                // We are in the pre-midnight segment; the end time belongs to tomorrow
                endDateTime = endDateTime.plusDays(1)
            }
        }

        // CASE 1: Current time is BEFORE the window opens
        if (now.isBefore(startDateTime)) {
            return startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 2: Current time is AFTER the window closes
        if (now.isAfter(endDateTime)) {
            // Roll forward to schedule for tomorrow's starting opening window
            return startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 3: Current time is INSIDE the window -> Schedule next interval trigger step
        val nextTriggerInterval = now.plusMinutes(intervalMinutes.toLong())

        return if (nextTriggerInterval.isBefore(endDateTime)) {
            nextTriggerInterval.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            // If the next interval lands past the closing threshold, sleep until tomorrow's opening window
            startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    companion object {
        private const val TAG = "WaterNotificationScheduler"
        private const val ALARM_REQUEST_CODE = 5001
    }
}