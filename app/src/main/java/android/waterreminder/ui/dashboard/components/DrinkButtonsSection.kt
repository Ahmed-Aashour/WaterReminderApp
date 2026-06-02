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

@Composable
fun DrinkButtonsSection(
    onPresetClick: (Int) -> Unit,
    onCustomAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.width(342.dp) // Conforms structurally to the global UI component footprint
    ) {
        // --- Component Section Header Label ---
        Text(
            text = "Drink Cups",
            style = TextStyle(
                fontFamily = AgbalumoFont,
                fontSize = 20.sp, // Matches Figma 'Section - 20/Auto' description rules
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // --- Buttons Alignment Dock Row ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp), // Calculates clean structural layout flow gaps
            verticalAlignment = Alignment.CenterVertically
        ) {
            val presets = listOf(250, 350, 500)

            // Loop through your decoupled components
            presets.forEach { amount ->
                PresetCupButton(
                    amountMl = amount,
                    onClick = onPresetClick
                )
            }

            Spacer(modifier = Modifier.weight(1f)) // Intuitively balances the layout gap leading up to the action switch

            // Inject global standalone add component
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