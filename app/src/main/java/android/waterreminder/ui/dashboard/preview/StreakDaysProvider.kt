package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakDayState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class StreakDaysProvider : PreviewParameterProvider<List<StreakDayState>> {
    override val values: Sequence<List<StreakDayState>> = sequenceOf(
        // Scenario A: Standard Mid-Week Progress
        listOf(
            StreakDayState("S", progress = 1.0f, isCurrentDay = false),
            StreakDayState("M", progress = 1.0f, isCurrentDay = false),
            StreakDayState("Tu", progress = 0.0f, isCurrentDay = false),
            StreakDayState("W", progress = 1.0f, isCurrentDay = false),
            StreakDayState("Th", progress = 0.4f, isCurrentDay = true),
            StreakDayState("F", progress = 0.0f, isCurrentDay = false),
            StreakDayState("S", progress = 0.0f, isCurrentDay = false)
        ),
        // Scenario B: Perfect Week Streak Locked In!
        listOf(
            StreakDayState("S", progress = 1.0f, isCurrentDay = false),
            StreakDayState("M", progress = 1.0f, isCurrentDay = false),
            StreakDayState("Tu", progress = 1.0f, isCurrentDay = false),
            StreakDayState("W", progress = 1.0f, isCurrentDay = false),
            StreakDayState("Th", progress = 1.0f, isCurrentDay = true),
            StreakDayState("F", progress = 1.0f, isCurrentDay = false),
            StreakDayState("S", progress = 1.0f, isCurrentDay = false)
        )
    )
}