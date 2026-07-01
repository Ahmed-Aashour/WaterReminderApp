package android.waterreminder.ui.dashboard

import android.waterreminder.R
import android.waterreminder.ui.core.components.Header
import android.waterreminder.ui.core.components.SquareIconButton
import android.waterreminder.ui.dashboard.components.DrinkButtonsSection
import android.waterreminder.ui.dashboard.components.HydrationStreakSection
import android.waterreminder.ui.dashboard.components.ProgressBar
import android.waterreminder.ui.dashboard.components.TodayHistorySection
import android.waterreminder.ui.dashboard.preview.DashboardScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    state: DashboardState,
    onAddWater: (Int) -> Unit,
    onCustomAddTrigger: () -> Unit,
    onDeleteLog: (DrunkCupHistory) -> Unit,
    onClearAllHistoryTrigger: () -> Unit,
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
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Header(
                title = stringResource(R.string.app_name),
                actionButton = {
                    SquareIconButton(
                        icon = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.dashboard_accessibility_settings_button),
                        onClick = onNavigateToSettings
                    )
                }
            )

            ProgressBar(
                state = state.progressDisplay,
            )

            DrinkButtonsSection(
                state = state.drinkButtons,
                onPresetClick = onAddWater,
                onCustomAddClick = onCustomAddTrigger
            )

            TodayHistorySection(
                historyItems = state.historyLogs,
                currentUnit = state.drinkButtons.unit,
                onDeleteLog = onDeleteLog,
                onClearAllHistoryTrigger = onClearAllHistoryTrigger,
                modifier = Modifier.weight(1f)
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
@Preview(
    name = "Dashboard - Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DashboardScreenPreview(
    @PreviewParameter(DashboardScreenStateProvider::class) state: DashboardState
) {
    ErtawyTheme {
        DashboardScreen(
            state = state,
            onAddWater = {},
            onCustomAddTrigger = {},
            onDeleteLog = {},
            onClearAllHistoryTrigger = {},
            onNavigateToSettings = {}
        )
    }
}
