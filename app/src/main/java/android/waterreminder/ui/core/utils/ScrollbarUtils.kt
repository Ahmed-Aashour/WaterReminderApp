package android.waterreminder.ui.core.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
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

        // Left Edge Mask Fade
        if (currentScroll > 0) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startX = 0f,
                    endX = fadeWidthPx
                ),
                blendMode = BlendMode.DstIn
            )
        }

        // Right Edge Mask Fade
        if (currentScroll < maxScroll) {
            drawRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startX = viewWidth - fadeWidthPx,
                    endX = viewWidth
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }