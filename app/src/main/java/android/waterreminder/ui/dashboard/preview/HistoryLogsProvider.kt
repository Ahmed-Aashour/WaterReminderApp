package android.waterreminder.ui.dashboard.preview

import android.waterreminder.ui.dashboard.DrunkCupHistory
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import kotlin.collections.listOf

class HistoryLogsProvider : PreviewParameterProvider<List<DrunkCupHistory>> {
    override val values: Sequence<List<DrunkCupHistory>> = sequenceOf(
        emptyList(),
        // Scenario A: Standard midday logging history
        listOf(
            DrunkCupHistory(id = 1, amountMl = 250, timeLogged = "07:00 AM"),
            DrunkCupHistory(id = 2, amountMl = 500, timeLogged = "08:00 AM")
        ),
        // Scenario B: Mid-volume overflow testing state
        listOf(
            DrunkCupHistory(id = 1, amountMl = 250, timeLogged = "07:00 AM"),
            DrunkCupHistory(id = 2, amountMl = 350, timeLogged = "08:00 AM"),
            DrunkCupHistory(id = 3, amountMl = 750, timeLogged = "09:00 AM")
        ),
        // Scenario C: High volume overflow testing state
        listOf(
            DrunkCupHistory(id = 1, amountMl = 250, timeLogged = "07:00 AM"),
            DrunkCupHistory(id = 2, amountMl = 350, timeLogged = "08:00 AM"),
            DrunkCupHistory(id = 3, amountMl = 500, timeLogged = "09:00 AM"),
            DrunkCupHistory(id = 4, amountMl = 750, timeLogged = "10:00 AM"),
            DrunkCupHistory(id = 5, amountMl = 1000, timeLogged = "11:00 AM")
        )
    )
}