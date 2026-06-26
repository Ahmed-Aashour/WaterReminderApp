package android.waterreminder.ui.dashboard.components

import android.waterreminder.data.entity.AppTheme
import android.waterreminder.ui.theme.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProgressBar(
    currentIntakeMl: Int,
    targetIntakeMl: Int,
    modifier: Modifier = Modifier
) {
    // Calculate progress ratio safely
    val progressPercentage = if (targetIntakeMl > 0) (currentIntakeMl * 100) / targetIntakeMl else 0

    // Coerce the fraction between 0f and 1f so it doesn't break layout boundaries if goals are exceeded
    val progressFraction = if (targetIntakeMl > 0) {
        (currentIntakeMl.toFloat() / targetIntakeMl.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // To make water fill up from the bottom, we invert the math layout line:
    val waterLevelLine = 1f - progressFraction

    // Resolve structural theme colors dynamically
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackBackgroundColor = MaterialTheme.colorScheme.primaryContainer
    val progressIndicatorColor = MaterialTheme.colorScheme.onBackground
    val cardFillColor = MaterialTheme.colorScheme.surfaceVariant

    // Card Container (Figma Box-sizing specs translated to exact dp sizes)
    Box(
        modifier = modifier
            .size(width = 342.dp, height = 170.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                Brush.verticalGradient(
                    waterLevelLine to Color.Transparent,
                    // Replaced raw WaterProgress token with cardFillColor (surfaceVariant)
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

            // --- LEFT SIDE: Circular Progress Container (Ellipse 15) ---
            Box(
                modifier = Modifier.size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Track Circle Layer
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidthPx = 8.dp.toPx()
                    val outerOutlineWidthPx = 12.dp.toPx()

                    // FIXED OUTLINE BORDER
                    drawCircle(
                        color = primaryColor,
                        radius = size.minDimension / 2,
                        style = Stroke(width = outerOutlineWidthPx)
                    )

                    // BACKGROUND TRACK RING
                    drawCircle(
                        color = trackBackgroundColor,
                        radius = size.minDimension / 2,
                        style = Stroke(width = strokeWidthPx)
                    )

                    // ACTIVE PROGRESS ARC LAYER
                    drawArc(
                        color = progressIndicatorColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                // Displaying the dynamic percentage key inside the circle
                val isGoalReached = currentIntakeMl >= targetIntakeMl

                Text(
                    text = if (isGoalReached && progressPercentage == 100) "✓" else "$progressPercentage%",
                    style = if (isGoalReached && progressPercentage == 100) {
                        // Boost font size slightly for the checkmark icon so it fills the inner circle nicely
                        MaterialTheme.typography.displayLarge
                    } else {
                        MaterialTheme.typography.headlineLarge
                    },
                    color = primaryColor
                )
            }

            // --- RIGHT SIDE: Quantity Target Tracker ---
            Text(
                text = "$currentIntakeMl /\n$targetIntakeMl ml",
                style = MaterialTheme.typography.headlineLarge,
                color = primaryColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Progress 25% - Light Mode", showBackground = true)
@Composable
fun ProgressBarPreview_25_Light() {
    ErtawyTheme(appTheme = AppTheme.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 500, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress 50% - Dark Mode", showBackground = true)
@Composable
fun ProgressBarPreview_50_Dark() {
    ErtawyTheme(appTheme = AppTheme.DARK) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ProgressBar(currentIntakeMl = 1000, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress 75% - Light Mode", showBackground = true)
@Composable
fun ProgressBarPreview_75_Light() {
    ErtawyTheme(appTheme = AppTheme.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 1500, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress 100% - Dark Mode", showBackground = true)
@Composable
fun ProgressBarPreview_100_Dark() {
    ErtawyTheme(appTheme = AppTheme.DARK) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ProgressBar(currentIntakeMl = 2000, targetIntakeMl = 2000)
        }
    }
}