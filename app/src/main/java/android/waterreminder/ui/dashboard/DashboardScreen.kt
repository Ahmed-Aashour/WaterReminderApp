package android.waterreminder.ui.dashboard

import android.waterreminder.ui.core.components.SettingsButton
import android.waterreminder.ui.core.components.StreakDayState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.waterreminder.ui.theme.ErtawyTheme
import android.waterreminder.ui.dashboard.components.Header
import android.waterreminder.ui.dashboard.components.ProgressBar
import android.waterreminder.ui.dashboard.components.DrinkButtonsSection
import android.waterreminder.ui.dashboard.components.TodayHistorySection
import android.waterreminder.ui.dashboard.components.DrunkCupHistory
import android.waterreminder.ui.dashboard.components.HydrationStreakSection
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme

@Composable
fun DashboardScreen(
    currentIntake: Int,
    targetIntake: Int,
    historyLogs: List<DrunkCupHistory>,
    streakDays: List<StreakDayState>,
    streakCountText: String,
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
                streakCountText = streakCountText
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
fun DashboardScreenLightPreview() {
    val mockHistory = listOf(
        DrunkCupHistory(amountMl = 250, count = 2),
        DrunkCupHistory(amountMl = 500, count = 1)
    )

    // Mocking standard layout tracking data
    val mockStreak = listOf(
        StreakDayState("S", progress = 1.0f, isCurrentDay = false),
        StreakDayState("M", progress = 1.0f, isCurrentDay = false),
        StreakDayState("Tu", progress = 0.0f, isCurrentDay = false),
        StreakDayState("W", progress = 1.0f, isCurrentDay = false),
        StreakDayState("Th", progress = 0.4f, isCurrentDay = true),
        StreakDayState("F", progress = 0.0f, isCurrentDay = false),
        StreakDayState("S", progress = 0.0f, isCurrentDay = false)
    )

    ErtawyTheme(darkTheme = false) {
        DashboardScreen(
            currentIntake = 1000,
            targetIntake = 2500,
            historyLogs = mockHistory,
            streakDays = mockStreak,
            streakCountText = "365 days hydrated!",
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
fun DashboardScreenDarkPreview() {
    val mockHistory = listOf(
        DrunkCupHistory(amountMl = 250, count = 3),
        DrunkCupHistory(amountMl = 350, count = 1),
        DrunkCupHistory(amountMl = 750, count = 1)
    )

    val mockStreak = listOf(
        StreakDayState("S", progress = 1.0f, isCurrentDay = false),
        StreakDayState("M", progress = 1.0f, isCurrentDay = false),
        StreakDayState("Tu", progress = 1.0f, isCurrentDay = false),
        StreakDayState("W", progress = 1.0f, isCurrentDay = false),
        StreakDayState("Th", progress = 1.0f, isCurrentDay = false),
        StreakDayState("F", progress = 0.1f, isCurrentDay = true),
        StreakDayState("S", progress = 0.0f, isCurrentDay = false)
    )

    ErtawyTheme(darkTheme = true) {
        DashboardScreen(
            currentIntake = 1850,
            targetIntake = 3000,
            historyLogs = mockHistory,
            streakDays = mockStreak,
            streakCountText = "5 days hydrated!",
            onAddWater = {},
            onCustomAddTrigger = {},
            onNavigateToSettings = {}
        )
    }
}