package android.waterreminder.ui.dashboard.components

import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// TODO: Animate button clicks
// TODO: Add Sounds
@Composable
fun PresetCupButton(
    amountMl: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(amountMl) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(50.dp), // Figma border-radius: 50px (Circle)
        modifier = modifier
            .size(60.dp) // Figma layout sizing: 60px x 60px
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${amountMl}ml",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview(name = "Preset Cup 250", showBackground = true)
@Composable
fun PresetCupButtonPreview_250() {
    ErtawyTheme {
        PresetCupButton(amountMl = 250, onClick = {})
    }
}

@Preview(name = "Preset Cup 350", showBackground = true)
@Composable
fun PresetCupButtonPreview_350() {
    ErtawyTheme {
        PresetCupButton(amountMl = 350, onClick = {})
    }
}

@Preview(name = "Preset Cup 500", showBackground = true)
@Composable
fun PresetCupButtonPreview_500() {
    ErtawyTheme {
        PresetCupButton(amountMl = 500, onClick = {})
    }
}