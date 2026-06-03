package android.waterreminder.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.dashboard.components.Header
import android.waterreminder.ui.core.components.SettingsButton // Notice the decoupled core import path!
import android.waterreminder.ui.dashboard.components.DrinkButtonsSection
import android.waterreminder.ui.dashboard.components.ProgressBar
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DashboardScreen(
    currentIntake: Int,
    targetIntake: Int,
    onAddWater: (Int) -> Unit,
    onCustomAddTrigger: () -> Unit,
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
            Header(
                title = "Ertawy",
                actionButton = { SettingsButton(onClick = onNavigateToSettings) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProgressBar(
                currentIntakeMl = currentIntake,
                targetIntakeMl = targetIntake
            )

            Spacer(modifier = Modifier.height(32.dp))

            DrinkButtonsSection(
                onPresetClick = onAddWater,
                onCustomAddClick = onCustomAddTrigger
            )

        }
    }
}

@Preview(
    name = "Full Dashboard Screen Viewport",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardScreenPreview() {
    ErtawyTheme {
        DashboardScreen(
            currentIntake = 500,
            targetIntake = 2000,
            onAddWater = {},
            onCustomAddTrigger = {},
            onNavigateToSettings = {}
        )
    }
}