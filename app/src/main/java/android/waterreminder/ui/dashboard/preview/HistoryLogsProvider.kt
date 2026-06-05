package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.model.DrunkCupHistory
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.collections.listOf

class HistoryLogsProvider : PreviewParameterProvider<List<DrunkCupHistory>> {
    override val values: Sequence<List<DrunkCupHistory>> = sequenceOf(
        emptyList(),
        // Scenario A: Standard midday logging history
        listOf(
            DrunkCupHistory(amountMl = 250, count = 2),
            DrunkCupHistory(amountMl = 500, count = 1)
        ),
        // Scenario B: Mid-volume overflow testing state
        listOf(
            DrunkCupHistory(amountMl = 250, count = 3),
            DrunkCupHistory(amountMl = 350, count = 1),
            DrunkCupHistory(amountMl = 750, count = 1)
        ),
        // Scenario C: High volume overflow testing state
        listOf(
            DrunkCupHistory(amountMl = 250, count = 3),
            DrunkCupHistory(amountMl = 350, count = 1),
            DrunkCupHistory(amountMl = 500, count = 2),
            DrunkCupHistory(amountMl = 750, count = 1),
            DrunkCupHistory(amountMl = 1000, count = 4)
        )
    )
}