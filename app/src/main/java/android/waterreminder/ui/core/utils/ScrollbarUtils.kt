package android.waterreminder.ui.core.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dynamically applies a blending alpha gradient layer to the edges
 * of a scrollable viewport box based on real-time layout movement parameters.
 */
fun Modifier.dynamicFadingEdges(
    state: ScrollState,
    fadeWidth: Dp = 32.dp
): Modifier = this
    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
    .drawWithContent {
        drawContent()
        val currentScroll = state.value
        val maxScroll = state.maxValue
        val viewWidth = size.width
        val fadeWidthPx = fadeWidth.toPx()

        if (maxScroll <= 0) return@drawWithContent

        val minEdgeContentAlpha = 0.35f
        val leftFadeAlpha = (currentScroll / fadeWidthPx).coerceIn(0f, 1f)
        val rightFadeAlpha = ((maxScroll - currentScroll) / fadeWidthPx).coerceIn(0f, 1f)

        if (leftFadeAlpha > 0f) {
            // Left Edge Mask Fade
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 1f - ((1f - minEdgeContentAlpha) * leftFadeAlpha)),
                        Color.Black
                    ),
                    startX = 0f,
                    endX = fadeWidthPx
                ),
                topLeft = Offset(0f, 0f),
                size = Size(fadeWidthPx, size.height),
                blendMode = BlendMode.DstIn
            )
        }

        if (rightFadeAlpha > 0f) {
            // Right Edge Mask Fade
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Black,
                        Color.Black.copy(alpha = 1f - ((1f - minEdgeContentAlpha) * rightFadeAlpha))
                    ),
                    startX = viewWidth - fadeWidthPx,
                    endX = viewWidth
                ),
                topLeft = Offset(viewWidth - fadeWidthPx, 0f),
                size = Size(fadeWidthPx, size.height),
                blendMode = BlendMode.DstIn
            )
        }
    }
