package android.waterreminder.ui.dashboard.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.waterreminder.ui.theme.AgbalumoFont
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.core.components.CustomAddButton
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.horizontalFadingEdge(): Modifier = this
    .graphicsLayer {
        // 1. Isolate the layer composition so blend modes don't bleed into the background canvas
        compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen
    }
    .drawWithContent {
        drawContent() // Render the child buttons first

        // 2. Define a clean gradient from fully visible to fully transparent across the last 32dp
        val fadeWidth = 32.dp.toPx()
        val gradientStart = size.width - fadeWidth

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startX = gradientStart,
                endX = size.width
            ),
            blendMode = BlendMode.DstIn // Keeps destination content only where the alpha brush is present
        )
    }

@Composable
fun DrinkButtonsSection(
    onPresetClick: (Int) -> Unit,
    onCustomAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                    .horizontalFadingEdge() // Fading
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