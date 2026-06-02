package android.waterreminder.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.dashboard.components.Header
import android.waterreminder.ui.core.components.SettingsButton // Notice the decoupled core import path!
import android.waterreminder.ui.dashboard.components.ProgressBar
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.ui.graphics.Color.White
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Assembling the parts cleanly
            Header(
                title = "Ertawy",
                actionButton = {
                    SettingsButton(onClick = onNavigateToSettings)
                }
            )

            Spacer(modifier = Modifier.height(24.dp)) // Spacing matching top constraints

            // 2. Mount the New Progress Bar Component
            ProgressBar(
                currentIntakeMl = 1600,
                targetIntakeMl = 2000
            )
        }
    }
}

@Preview(
    name = "Full Dashboard Screen Viewport",
    showBackground = true,
    showSystemUi = true // This renders the Android status bar and navigation buttons for high fidelity
)
@Composable
fun DashboardScreenPreview() {
    ErtawyTheme {
        DashboardScreen(onNavigateToSettings = {})
    }
}