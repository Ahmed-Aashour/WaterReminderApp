package android.waterreminder.ui.core.components

import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.theme.WaterPrimary
import android.waterreminder.ui.theme.WaterWhite
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// TODO: Animate button clicks
// TODO: Add Sounds
@Composable
fun CustomAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = WaterPrimary,
            contentColor = WaterWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(17.dp), // Figma border-radius: 17px
        modifier = modifier.size(30.dp) // Figma size: 30px x 30px
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Custom Add Amount",
                modifier = Modifier.size(16.dp), // Safe internal balance mapping Vector 2 bounding box
                tint = Color.White
            )
        }
    }
}

@Preview(name = "Global Custom Add Component", showBackground = true)
@Composable
fun CustomAddButtonPreview() {
    ErtawyTheme {
        Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
            CustomAddButton(onClick = {})
        }
    }
}