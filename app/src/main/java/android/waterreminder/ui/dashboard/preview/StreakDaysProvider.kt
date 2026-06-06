package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakDayState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class StreakDaysProvider : PreviewParameterProvider<List<StreakDayState>> {
    override val values: Sequence<List<StreakDayState>> = sequenceOf(
        // Scenario A: Standard Mid-Week Progress
        listOf(
            StreakDayState("S", progress = 1.0f),
            StreakDayState("M", progress = 1.0f),
            StreakDayState("Tu", progress = 0.0f),
            StreakDayState("W", progress = 1.0f),
            StreakDayState("Th", progress = 0.4f),
            StreakDayState("F", progress = 0.0f),
            StreakDayState("S", progress = 0.0f)
        ),
        // Scenario B: Perfect Week Streak Locked In!
        listOf(
            StreakDayState("S", progress = 1.0f),
            StreakDayState("M", progress = 1.0f),
            StreakDayState("Tu", progress = 1.0f),
            StreakDayState("W", progress = 1.0f),
            StreakDayState("Th", progress = 1.0f),
            StreakDayState("F", progress = 1.0f),
            StreakDayState("S", progress = 1.0f)
        )
    )
}