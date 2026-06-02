package android.waterreminder.ui.core.utils

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Effect A: Dynamically applies a blending alpha gradient layer to the edges
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

/**
 * Effect B: Renders a clean, persistent horizontal track and proportional position thumb
 * indicator at the absolute bottom coordinates of the host layout box.
 */
fun Modifier.simpleHorizontalScrollbar(
    state: ScrollState,
    scrollbarWidth: Dp = 4.dp,
    indicatorColor: Color,
    trackColor: Color
): Modifier = this.drawWithContent {
    drawContent()
    val maxScroll = state.maxValue.toFloat()
    if (maxScroll <= 0f) return@drawWithContent

    val currentScroll = state.value.toFloat()
    val viewWidth = size.width
    val thickness = scrollbarWidth.toPx()
    val yOffset = size.height - thickness

    // 1. Render Track Line Background
    drawRect(
        color = trackColor,
        topLeft = Offset(x = 0f, y = yOffset),
        size = Size(width = viewWidth, height = thickness)
    )

    // 2. Compute Proportional Thumb Mapping Width Metrics
    val totalContentWidth = viewWidth + maxScroll
    val indicatorWidth = (viewWidth / totalContentWidth) * viewWidth
    val scrollRatio = currentScroll / maxScroll
    val indicatorX = (viewWidth - indicatorWidth) * scrollRatio

    // 3. Render Active Position Thumb Overlay
    drawRect(
        color = indicatorColor,
        topLeft = Offset(x = indicatorX, y = yOffset),
        size = Size(width = indicatorWidth, height = thickness)
    )
}