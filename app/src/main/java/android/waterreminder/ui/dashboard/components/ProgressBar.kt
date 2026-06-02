package android.waterreminder.ui.dashboard.components

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.theme.ErtawyTypography
import android.waterreminder.ui.theme.WaterPrimary
import android.waterreminder.ui.theme.WaterProgress

@Composable
fun ProgressBar(
    currentIntakeMl: Int,
    targetIntakeMl: Int,
    modifier: Modifier = Modifier
) {
    // Calculate progress ratio safely
    val progressPercentage = if (targetIntakeMl > 0) (currentIntakeMl * 100) / targetIntakeMl else 0
    val progressFraction = if (targetIntakeMl > 0) currentIntakeMl.toFloat() / targetIntakeMl.toFloat() else 0f

    // Card Container (Figma Box-sizing specs translated to exact dp sizes)
    Box(
        modifier = modifier
            .size(width = 342.dp, height = 170.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                // Figma linear-gradient definition translation
                Brush.verticalGradient(
                    0.2018f to Color.Transparent,
                    0.2019f to WaterProgress
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
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 8.dp.toPx())
                    )
                    // Animated Arc Tracking Indicator Rim
                    drawArc(
                        color = WaterPrimary,
                        startAngle = -90f,
                        sweepAngle = 360f * progressFraction,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
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

@Preview(name = "Progress Bar Component", showBackground = true)
@Composable
fun ProgressBarPreview() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            ProgressBar(currentIntakeMl = 1500, targetIntakeMl = 2000)
        }
    }
}