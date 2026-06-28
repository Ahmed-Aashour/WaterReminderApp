package android.waterreminder.ui.dashboard.preview

import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.dashboard.DisplayIntakeState
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ProgressBarStateProvider : PreviewParameterProvider<DisplayIntakeState> {
    override val values: Sequence<DisplayIntakeState> = listOf(
        // Scenario 1: 25% progress in Milliliters
        DisplayIntakeState(
            currentLabel = "500",
            targetLabel = "2000",
            unit = AppUnit.ML,
            progressFraction = 0.25f,
            progressPercentage = 25
        ),
        // Scenario 2: 50% progress in Fluid Ounces
        DisplayIntakeState(
            currentLabel = "34",
            targetLabel = "68",
            unit = AppUnit.OZ,
            progressFraction = 0.50f,
            progressPercentage = 50
        ),
        // Scenario 3: 75% progress in Milliliters
        DisplayIntakeState(
            currentLabel = "1500",
            targetLabel = "2000",
            unit = AppUnit.ML,
            progressFraction = 0.75f,
            progressPercentage = 75
        ),
        // Scenario 4: 100% progress (Goal Reached) boundary test
        DisplayIntakeState(
            currentLabel = "2000",
            targetLabel = "2000",
            unit = AppUnit.ML,
            progressFraction = 1.0f,
            progressPercentage = 100
        )
    ).asSequence()
}