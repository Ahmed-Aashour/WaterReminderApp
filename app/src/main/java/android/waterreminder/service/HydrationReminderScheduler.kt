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

/**
 * Centrally manages scheduling and canceling system-level background alarms for hydration reminders.
 *
 * This class interfaces directly with Android's [AlarmManager] to set wake-up triggers that survive
 * device Doze modes. It safely navigates Android 12+ (API 31) and Android 14+ (API 34) exact alarm
 * permission constraints by enforcing automatic inexact fallbacks and capturing [SecurityException] conditions.
 *
 * @property context The application context used to resolve the system alarm service and build intents.
 */
class HydrationReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Schedules the next recurring hydration reminder broadcast.
     *
     * Depending on system permissions and Android platform version, this will attempt to fire an exact
     * alarm via [AlarmManager.setExactAndAllowWhileIdle] to minimize delivery drifting. If permissions
     * are missing or revoked by the user, it transparently drops back to low-battery consumption, inexact
     * windows via [AlarmManager.setAndAllowWhileIdle].
     *
     * @param startTime The daily boundary [LocalTime] indicating when notifications are allowed to start.
     * @param endTime The daily boundary [LocalTime] indicating when notifications must stop.
     * @param intervalMinutes The frequency tracking step distance used to space consecutive reminders.
     */
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
            // Guard rule verifying modern runtime permission realities for API 31 through API 34+
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

    /**
     * Cancels any active scheduled hydration reminder alarms currently registered within the Android OS.
     *
     * Leverages [PendingIntent.FLAG_NO_CREATE] to cleanly verify if an active intent token context is
     * floating in memory before dispatching a cancel signal, keeping OS resource interaction safe.
     */
    fun cancelReminders() {
        val intent = Intent(context, HydrationReminderReceiver::class.java)

        val existingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (existingIntent != null) {
            alarmManager.cancel(existingIntent)
            existingIntent.cancel() // Nullify the wrapper token explicitly
            Log.d(TAG, "Active hydration reminders found and canceled.")
        } else {
            Log.d(TAG, "No active reminder alarms were scheduled. Cancel skipped.")
        }
    }

    /**
     * Fallback execution block that hooks an inexact wake-up intent into the [AlarmManager].
     * Allows the OS to batched schedule triggers alongside other system apps to minimize battery depletion.
     */
    private fun scheduleInexactAlarm(triggerTimeMs: Long, pendingIntent: PendingIntent) {
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTimeMs,
            pendingIntent
        )
        Log.d(TAG, "Next inexact reminder scheduled successfully for epoch timestamp: $triggerTimeMs")
    }

    /**
     * Calculates the exact Epoch millisecond timestamp for the next valid alarm trigger.
     *
     * This logic manages calculation windows across three specific states:
     * 1. Current clock time resides before the tracking window opens.
     * 2. Current clock time resides after the tracking window closes.
     * 3. Current clock time is actively inside the valid tracking window bounds.
     *
     * It natively handles overnight intervals (e.g., custom user fasting schedules running from
     * 18:45 PM to 04:15 AM into the following calendar morning) without throwing back-date errors.
     *
     * @return A [Long] representation of the exact UTC epoch target timestamp.
     */
    private fun calculateNextTriggerMillis(
        startTime: LocalTime,
        endTime: LocalTime,
        intervalMinutes: Int
    ): Long {
        val now = LocalDateTime.now()

        // Establish base anchor datetime mappings
        var startDateTime = now.with(startTime)
        var endDateTime = now.with(endTime)

        // Adjust tracking contexts for overnight active shifts
        if (endTime.isBefore(startTime)) {
            if (now.toLocalTime().isBefore(endTime)) {
                // Currently in the post-midnight segment of an overnight window
                startDateTime = startDateTime.minusDays(1)
            } else {
                // Currently in the pre-midnight segment; the end target occurs on the following day
                endDateTime = endDateTime.plusDays(1)
            }
        }

        // CASE 1: Current clock time is BEFORE the window opens
        if (now.isBefore(startDateTime)) {
            return startDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 2: Current clock time is AFTER the window has closed
        if (now.isAfter(endDateTime)) {
            // Roll forward to schedule for tomorrow's starting opening window
            return startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 3: Current clock time is inside the tracking window bounds -> project forward by step distance
        val nextTriggerInterval = now.plusMinutes(intervalMinutes.toLong())

        return if (nextTriggerInterval.isBefore(endDateTime)) {
            nextTriggerInterval.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            // Drifting outside closing thresholds forces system dormancy until the next morning opening window
            startDateTime.plusDays(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }

    companion object {
        private const val TAG = "HydrationReminderScheduler"

        /**
         * Unique application token used to isolate this component's scheduling transactions
         * from other pending intents inside the device OS layer.
         */
        private const val ALARM_REQUEST_CODE = 5001
    }
}