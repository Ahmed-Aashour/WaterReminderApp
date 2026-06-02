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
 * Applies a combined visual effect for scrollable rows:
 * 1. A permanent horizontal scrollbar tracking mechanism at the bottom edge.
 * 2. Dynamic fading edges on the left and right sides based on scroll position.
 */
fun Modifier.scrollEffects(
    state: ScrollState,
    scrollbarWidth: Dp = 4.dp,
    indicatorColor: Color,
    trackColor: Color,
    fadeWidth: Dp = 32.dp
): Modifier = this
    // 1. Isolate the layer composition so alpha blending mask doesn't bleed through
    .graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
    .drawWithContent {
        drawContent() // Render the baseline inner cup buttons first

        val currentScroll = state.value
        val maxScroll = state.maxValue
        val viewWidth = size.width

        if (maxScroll <= 0) return@drawWithContent // Auto-skip effects if content fits completely

        // --- PART A: DYNAMIC FADING EDGES MASK ---
        val fadeWidthPx = fadeWidth.toPx()

        // Apply left edge fade if user has scrolled right from the starting line
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

        // Apply right edge fade if there is still content remaining to be scrolled
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

        // --- PART B: STATIC PERSISTENT SCROLLBAR ---
        val thickness = scrollbarWidth.toPx()
        val yOffset = size.height - thickness
        val maxScrollFloat = maxScroll.toFloat()
        val currentScrollFloat = currentScroll.toFloat()

        // 1. Draw Background Track Line
        drawRect(
            color = trackColor,
            topLeft = Offset(x = 0f, y = yOffset),
            size = Size(width = viewWidth, height = thickness)
        )

        // 2. Calculate proportional dimensions for the physical indicator thumb
        val totalContentWidth = viewWidth + maxScrollFloat
        val indicatorWidth = (viewWidth / totalContentWidth) * viewWidth

        // Map list tracking position to track window space bounds smoothly
        val scrollRatio = currentScrollFloat / maxScrollFloat
        val indicatorX = (viewWidth - indicatorWidth) * scrollRatio

        // 3. Draw Active Position Indicator Drag Overlay
        drawRect(
            color = indicatorColor,
            topLeft = Offset(x = indicatorX, y = yOffset),
            size = Size(width = indicatorWidth, height = thickness)
        )
    }