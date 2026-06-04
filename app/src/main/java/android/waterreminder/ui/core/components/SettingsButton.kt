package android.waterreminder.ui.core.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.theme.ErtawyTheme

@Composable
fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            // Automatically maps readable text/icon colors based on the current theme state
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        shape = RoundedCornerShape(5.dp), // Figma border-radius: 5px
        modifier = modifier.size(42.dp)  // Figma 42px width and height
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimary // Follows the parent content tint color
            )
        }
    }
}

// --- PREVIEWS ---

@Preview(name = "Settings Button - Light Mode", showBackground = true)
@Composable
fun SettingsButtonLightPreview() {
    ErtawyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            SettingsButton(onClick = {})
        }
    }
}

@Preview(
    name = "Settings Button - Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SettingsButtonDarkPreview() {
    ErtawyTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            SettingsButton(onClick = {})
        }
    }
}