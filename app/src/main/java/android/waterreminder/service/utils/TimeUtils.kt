package android.waterreminder.service.utils

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeUtils {
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    /**
     * Parses a string like "07:00 AM" or "09:00 PM" into
     * a standard LocalTime object.
     */
    fun parseTimeString(timeStr: String): LocalTime {
        return LocalTime.parse(timeStr.trim(), timeFormatter)
    }

    /**
     * Calculates the exact Epoch millisecond timestamp for
     * the next alarm trigger.
     */
    fun calculateNextTriggerMillis(
        startTimeStr: String,
        endTimeStr: String,
        intervalMinutes: Int
    ): Long {
        val now = LocalDateTime.now()
        val startLocalTime = parseTimeString(startTimeStr)
        val endLocalTime = parseTimeString(endTimeStr)

        // Map today's explicit start and end parameters
        var startDateTime = now.with(startLocalTime)
        var endDateTime = now.with(endLocalTime)

        // Handle overnight shifts (e.g., Fasting Mode shifting from 6:45 PM to 4:15 AM tomorrow)
        if (endLocalTime.isBefore(startLocalTime)) {
            if (now.toLocalTime().isBefore(endLocalTime)) {
                // We are currently in the post-midnight segment of the overnight window
                startDateTime = startDateTime.minusDays(1)
            } else {
                // We are in the pre-midnight segment; the end time belongs to tomorrow
                endDateTime = endDateTime.plusDays(1)
            }
        }

        // CASE 1: Current time is BEFORE the window opens
        if (now.isBefore(startDateTime)) {
            return startDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 2: Current time is AFTER the window closes
        if (now.isAfter(endDateTime)) {
            // Roll forward to schedule for tomorrow's starting opening window
            return startDateTime.plusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }

        // CASE 3: Current time is INSIDE the window -> Schedule next interval trigger step
        val nextTriggerInterval = now.plusMinutes(intervalMinutes.toLong())

        return if (nextTriggerInterval.isBefore(endDateTime)) {
            nextTriggerInterval.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            // If the next interval lands past the closing threshold, sleep until tomorrow's opening window
            startDateTime.plusDays(1).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}