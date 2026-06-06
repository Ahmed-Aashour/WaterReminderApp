package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.StreakDayState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

data class NodePreviewScenario(
    val state: StreakDayState,
    val isCurrentDay: Boolean
)

class HydrationNodeStateProvider : PreviewParameterProvider<NodePreviewScenario> {
    override val values: Sequence<NodePreviewScenario> = sequenceOf(
        NodePreviewScenario(
            state = StreakDayState(dayLabel = "M", progress = 1.0f),
            isCurrentDay = false
        ),
        NodePreviewScenario(
            state = StreakDayState(dayLabel = "Tu", progress = 0.4f),
            isCurrentDay = true
        ),
        NodePreviewScenario(
            state = StreakDayState(dayLabel = "W", progress = 0.0f),
            isCurrentDay = false
        )
    )
}