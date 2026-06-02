package android.waterreminder.ui.dashboard.components

import android.waterreminder.ui.core.components.CustomAddButton
import android.waterreminder.ui.theme.AgbalumoFont
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Custom extension modifier that dynamically scales a fading alpha mask on both
 * the left and right edges depending on the list's real-time scrolling progression.
 */
fun Modifier.dynamicFadingEdges(scrollState: ScrollState, fadeWidth: Float = 32f): Modifier = this
    .graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }
    .drawWithContent {
        drawContent() // Render the baseline preset buttons first

        // Calculate dynamic values
        val currentScroll = scrollState.value
        val maxScroll = scrollState.maxValue
        val viewWidth = size.width
        val fadeWidthPx = fadeWidth.dp.toPx()

        // Draw left fading edge if we have scrolled away from the starting line
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

        // Draw right fading edge ONLY if we haven't reached the end boundary buffer zone yet
        if (currentScroll < maxScroll && maxScroll > 0) {
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

@Composable
fun DrinkButtonsSection(
    onPresetClick: (Int) -> Unit,
    onCustomAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Instantiate and track the scroll state so our modifier can access its layout state parameters
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.width(342.dp) // Bound tightly to Figma's structural block footprint
    ) {
        // --- Section Title ---
        Text(
            text = "Drink Cups",
            style = TextStyle(
                fontFamily = AgbalumoFont,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // --- Layout Grid Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // --- THE SCROLL BOX CONTAINER ---
            // Takes up all available space up to the Custom Add Button block.
            Row(
                modifier = Modifier
                    .weight(1f) // Fills remaining space dynamically
                    .padding(end = 16.dp) // Prevents buttons from kissing the custom add button
                    .dynamicFadingEdges(scrollState) // Fading
                    .horizontalScroll(rememberScrollState()), // Enables frictionless horizontal scrolling
                horizontalArrangement = Arrangement.spacedBy(16.dp), // Space gaps between preset buttons
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Expanded list to demonstrate scroll capability cleanly
                val presets = listOf(250, 350, 500, 750, 1000)

                presets.forEach { amount ->
                    PresetCupButton(
                        amountMl = amount,
                        onClick = onPresetClick
                    )
                }
            }

            // --- FIXED CUSTOM ADD ACTION WINDOW ---
            // Sits securely on the right edge, unaffected by the scrolling container content
            CustomAddButton(
                onClick = onCustomAddClick
            )
        }
    }
}

@Preview(name = "Full Input Buttons Section Viewport", showBackground = true)
@Composable
fun DrinkButtonsSectionPreview() {
    ErtawyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            DrinkButtonsSection(onPresetClick = {}, onCustomAddClick = {})
        }
    }
}