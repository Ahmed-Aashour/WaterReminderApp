package android.waterreminder.ui.dashboard

import android.waterreminder.ui.core.components.SettingsButton
import android.waterreminder.ui.core.components.StreakDayState
import android.waterreminder.ui.dashboard.components.*
import android.waterreminder.ui.dashboard.preview.DashboardPreviewState
import android.waterreminder.ui.dashboard.preview.DashboardScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
    currentIntake: Int,
    targetIntake: Int,
    historyLogs: List<DrunkCupHistory>,
    streakDays: List<StreakDayState>,
    streakCount: Int,
    onAddWater: (Int) -> Unit,
    onCustomAddTrigger: () -> Unit,
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

            Spacer(modifier = Modifier.height(32.dp))

            TodayHistorySection(
                historyItems = historyLogs
            )

            Spacer(modifier = Modifier.height(32.dp))

            HydrationStreakSection(
                streakDays = streakDays,
                streakCount = streakCount
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
    @PreviewParameter(DashboardScreenStateProvider::class) state: DashboardPreviewState
) {
    ErtawyTheme(darkTheme = false) {
        DashboardScreen(
            currentIntake = state.currentIntake,
            targetIntake = state.targetIntake,
            historyLogs = state.historyLogs,
            streakDays = state.streakDays,
            streakCount = state.streakCount,
            onAddWater = {},
            onCustomAddTrigger = {},
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
    @PreviewParameter(DashboardScreenStateProvider::class) state: DashboardPreviewState
) {
    ErtawyTheme(darkTheme = true) {
        DashboardScreen(
            currentIntake = state.currentIntake,
            targetIntake = state.targetIntake,
            historyLogs = state.historyLogs,
            streakDays = state.streakDays,
            streakCount = state.streakCount,
            onAddWater = {},
            onCustomAddTrigger = {},
            onNavigateToSettings = {}
        )
    }
}