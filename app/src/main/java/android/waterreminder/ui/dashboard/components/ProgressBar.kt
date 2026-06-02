package android.waterreminder.ui.dashboard.components

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

    // Card Container (Figma Box-sizing specs translated to exact dp sizes)
    Box(
        modifier = modifier
            .size(width = 342.dp, height = 170.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                // Figma linear-gradient definition translation
                Brush.verticalGradient(
                    // Dynamic color anchors adapt live to user state tracking
                    waterLevelLine to Color.Transparent,
                    (waterLevelLine + 0.001f).coerceIn(0f, 1f) to WaterProgress
                )
            )
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(15.dp))
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
                    val outerOutlineWidthPx = 12.dp.toPx() // Slightly wider to sit behind the track

                    // FIXED OUTLINE BORDER
                    // Draws a crisp, solid white backdrop ring to separate the progress track from the fluid gradient background
                    drawCircle(
                        color = WaterPrimary,
                        radius = size.minDimension / 2,
                        style = Stroke(width = outerOutlineWidthPx)
                    )

                    // BACKGROUND TRACK RING
                    // The translucent blue track container inside the white outline bounds
                    drawCircle(
                        color = WaterPrimary,
                        radius = size.minDimension / 2,
                        style = Stroke(width = strokeWidthPx)
                    )

                    // ACTIVE PROGRESS ARC LAYER
                    // The actual dynamic filling indicator ring drawn neatly on top
                    drawArc(
                        color = WaterLight,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }

                // Displaying the dynamic percentage string inside the circle
                Text(
                    text = "$progressPercentage%",
                    style = ErtawyTypography.titleStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // --- RIGHT SIDE: Quantity Target Tracker ---
            Text(
                text = "$currentIntakeMl /\n$targetIntakeMl ml",
                style = ErtawyTypography.titleStyle,
                color = MaterialTheme.colorScheme.primary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Preview(name = "Progress Bar 25%", showBackground = true)
@Composable
fun ProgressBarPreview_25() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 500, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress Bar 50%", showBackground = true)
@Composable
fun ProgressBarPreview_50() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 1000, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress Bar 75%", showBackground = true)
@Composable
fun ProgressBarPreview_75() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 1500, targetIntakeMl = 2000)
        }
    }
}

@Preview(name = "Progress Bar 100%", showBackground = true)
@Composable
fun ProgressBarPreview_100() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 2000, targetIntakeMl = 2000)
        }
    }
}