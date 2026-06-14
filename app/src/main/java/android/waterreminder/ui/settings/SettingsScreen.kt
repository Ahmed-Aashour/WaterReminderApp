package android.waterreminder.ui.settings

import android.content.res.Configuration
import android.waterreminder.data.store.SettingsState
import android.waterreminder.ui.settings.components.HydrationFrame
import android.waterreminder.ui.settings.components.LegalLinksFrame
import android.waterreminder.ui.settings.components.NotificationsFrame
import android.waterreminder.ui.settings.components.PreferencesFrame
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onNavigateBack: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onUpdateMeasurementUnit: (String) -> Unit,
    onUpdateFastingState: (Boolean) -> Unit,
    onUpdateNotificationInterval: (Int) -> Unit,
    onUpdateReminderWindow: (String, String) -> Unit,
    onUpdateTheme: (String) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(start = 30.dp, end = 30.dp, top = 24.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            HydrationFrame(
                dailyGoal = state.dailyGoalMl,
                unit = state.measurementUnit,
                onGoalClick = { /* Launch integer input choice sheet */ },
                onUnitClick = {
                    val nextUnit = if(state.measurementUnit == "ml") "oz" else "ml"
                    onUpdateMeasurementUnit(nextUnit)
                }
            )

            NotificationsFrame(
                isNotificationEnabled = true, // TODO: Add the toggle setting
                frequencyMinutes = state.notificationInterval,
                reminderWindow = "${state.activeStartHour} - ${state.activeEndHour}",
                isFastingMode = state.isFasting,
                onIntervalToggle = { enabled -> onUpdateNotificationInterval(if(enabled) 60 else 0) },
                onFrequencyClick = { /* Launch frequency dialog options */ },
                onWindowClick = { onUpdateReminderWindow("08:00", "22:00") },
                onFastingToggle = onUpdateFastingState
            )

            PreferencesFrame(
                theme = state.theme,
                language = state.language,
                onThemeClick = { /* Open theme picker menu */ },
                onLanguageClick = { /* Open language selector */ }
            )

            LegalLinksFrame(
                onAboutClick = { },
                onFeedbackClick = { },
                onTermsClick = { },
                onAcknowledgementsClick = { }
            )

            Text(
                text = "version 1.0.1",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Light Mode", group = "Themes", showBackground = true)
@Preview(name = "Dark Mode", group = "Themes", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenPreview(
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsState
) {
    ErtawyTheme {
        SettingsScreen(
            state = state,
            onNavigateBack = {},
            onUpdateDailyGoal = {},
            onUpdateMeasurementUnit = {},
            onUpdateFastingState = {},
            onUpdateNotificationInterval = {},
            onUpdateReminderWindow = { _, _ -> },
            onUpdateTheme = {},
            onUpdateLanguage = {}
        )
    }
}