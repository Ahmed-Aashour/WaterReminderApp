package android.waterreminder.ui.dashboard.components

import android.content.res.Configuration
import android.waterreminder.R
import android.waterreminder.ui.dashboard.DisplayIntakeState
import android.waterreminder.ui.dashboard.preview.ProgressBarStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    state: DisplayIntakeState,
    modifier: Modifier = Modifier
) {
    val waterLevelLine = 1f - state.progressFraction
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    val progressIndicatorColor = MaterialTheme.colorScheme.onBackground
    val cardFillColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .size(width = 342.dp, height = 170.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.verticalGradient(
                    waterLevelLine to Color.Transparent,
                    (waterLevelLine + 0.001f).coerceIn(0f, 1f) to cardFillColor
                )
            )
            .border(2.dp, primaryColor, RoundedCornerShape(15.dp))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier.size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 8.dp.toPx()
                    val outerOutlineWidthPx = 12.dp.toPx()

                    drawCircle(color = primaryColor, radius = size.minDimension / 2, style = Stroke(width = outerOutlineWidthPx))
                    drawCircle(color = trackBackgroundColor, radius = size.minDimension / 2, style = Stroke(width = strokeWidthPx))
                    drawArc(
                        color = progressIndicatorColor,
                        startAngle = -90f,
                        sweepAngle = 360f * state.progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                val isGoalReached = state.progressPercentage >= 100
                Text(
                    text = if (isGoalReached) {
                        stringResource(R.string.dashboard_progress_goal_reached)
                    } else {
                        stringResource(R.string.dashboard_progress_percentage_format, state.progressPercentage)
                    },
                    style = if (isGoalReached) MaterialTheme.typography.displayLarge else MaterialTheme.typography.headlineLarge,
                    color = primaryColor
                )
            }

            val localizedUnitLabel = stringResource(id = state.unit.unitRes)

            Text(
                text = "${state.currentLabel} / ${state.targetLabel} $localizedUnitLabel",
                style = MaterialTheme.typography.headlineLarge,
                color = primaryColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "ProgressBar Themes",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    group = "ProgressBar Themes",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun ProgressBarPreview(
    @PreviewParameter(ProgressBarStateProvider::class)
    displayState: DisplayIntakeState
) {
    ErtawyTheme {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ProgressBar(state = displayState)
        }
    }
}