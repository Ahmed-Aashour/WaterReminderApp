package android.waterreminder.ui.dashboard

import android.waterreminder.ui.core.components.Header
import android.waterreminder.ui.core.components.SquareIconButton
import android.waterreminder.ui.dashboard.components.*
import android.waterreminder.ui.dashboard.preview.DashboardScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAddWater: (Int) -> Unit,
    onCustomAddTrigger: () -> Unit,
    onDeleteLog: (DrunkCupHistory) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Header(
                title = "Ertawy",
                actionButton = {
                    SquareIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = "Settings",
                        onClick = onNavigateToSettings
                    )
                }
            )

            ProgressBar(
                currentIntakeMl = state.currentIntake,
                targetIntakeMl = state.targetIntake
            )

            DrinkButtonsSection(
                onPresetClick = onAddWater,
                onCustomAddClick = onCustomAddTrigger
            )

            TodayHistorySection(
                historyItems = state.historyLogs,
                onDeleteLog = onDeleteLog
            )

            HydrationStreakSection(
                state = state.streakSection
            )
        }
    }
}

@Preview(
    name = "Dashboard - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DashboardScreenLightPreview(
    @PreviewParameter(DashboardScreenStateProvider::class) state: DashboardState
) {
    ErtawyTheme(darkTheme = false) {
        DashboardScreen(
            state = state,
            onAddWater = {},
            onCustomAddTrigger = {},
            onDeleteLog = {},
            onNavigateToSettings = {}
        )
    }
}

@Preview(
    name = "Dashboard - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DashboardScreenDarkPreview(
    @PreviewParameter(DashboardScreenStateProvider::class) state: DashboardState
) {
    ErtawyTheme(darkTheme = true) {
        DashboardScreen(
            state = state,
            onAddWater = {},
            onCustomAddTrigger = {},
            onDeleteLog = {},
            onNavigateToSettings = {}
        )
    }
}