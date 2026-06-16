package android.waterreminder.ui.settings

import android.content.res.Configuration
import android.waterreminder.ui.core.components.Header
import android.waterreminder.ui.core.components.SquareIconButton
import android.waterreminder.ui.settings.components.DailyGoalDialog
import android.waterreminder.ui.settings.components.FrequencyDialog
import android.waterreminder.ui.settings.components.HydrationFrame
import android.waterreminder.ui.settings.components.LegalLinksFrame
import android.waterreminder.ui.settings.components.NotificationsFrame
import android.waterreminder.ui.settings.components.PeriodDialog
import android.waterreminder.ui.settings.components.PreferencesFrame
import android.waterreminder.ui.settings.components.UnitDialog
import android.waterreminder.ui.settings.preview.SettingsScreenStateProvider
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    validationEvents: SharedFlow<String>,
    onNavigateBack: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onUpdateCustomDailyGoalString: (String) -> Unit,
    onUpdateUnit: (String) -> Unit,
    onUpdateNotificationToggle: (Boolean) -> Unit,
    onUpdateFrequency: (Int) -> Unit,
    onUpdateStartAndEndTimes: (String, String) -> Unit,
    onUpdateFastingState: (Boolean) -> Unit,
    onUpdateTheme: (String) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var showFrequencyDialog by remember { mutableStateOf(false) }
    var showPeriodDialog by remember { mutableStateOf(false) }

    if (showGoalDialog) {
        DailyGoalDialog(
            currentGoalMl = state.dailyGoalMl,
            currentUnit = state.unit,
            predefinedOptions = state.predefinedGoals,
            validationEvents = validationEvents,
            onDismiss = { showGoalDialog = false },
            onConfirm = { selectedGoal ->
                onUpdateDailyGoal(selectedGoal)
                showGoalDialog = false // Clean options are safe to dismiss immediately
            },
            onConfirmCustomString = { customString ->
                onUpdateCustomDailyGoalString(customString)
                // Do not auto-dismiss! If validation passes, uiState updates close context via parent checks if desired,
                // or user can click out. If it fails, error stays up for rectification.
            }
        )
    }

    if (showUnitDialog) {
        UnitDialog(
            currentUnit = state.unit,
            supportedUnits = state.supportedUnits,
            onUnitSelected = { selectedUnit -> onUpdateUnit(selectedUnit) },
            onDismiss = { showUnitDialog = false }
        )
    }

    if (showFrequencyDialog) {
        FrequencyDialog(
            currentFrequency = state.frequency,
            frequencyOptions = state.supportedFrequencies,
            onFrequencySelected = { selectedMinutes ->
                onUpdateFrequency(selectedMinutes)
            },
            onDismiss = { showFrequencyDialog = false }
        )
    }

    if (showPeriodDialog) {
        PeriodDialog(
            currentStartTime = state.startTime,
            currentEndTime = state.endTime,
            onDismiss = { showPeriodDialog = false },
            onConfirm = { startTime, endTime ->
                onUpdateStartAndEndTimes(startTime, endTime)
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp, top = 24.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Header(
                title = "Settings",
                actionButton = {
                    SquareIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return Button",
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateBack
                    )
                }
            )

            HydrationFrame(
                dailyGoal = state.dailyGoalMl,
                unit = state.unit,
                onGoalClick = { showGoalDialog = true },
                onUnitClick = { showUnitDialog = true }
            )

            NotificationsFrame(
                isNotificationEnabled = state.areNotificationsEnabled,
                frequencyMinutes = state.frequency,
                reminderWindow = "${state.activeStartTime} - ${state.activeEndTime}",
                isFastingMode = state.isFasting,
                onNotificationToggle = onUpdateNotificationToggle,
                onFrequencyClick = { showFrequencyDialog = true },
                onWindowClick = { showPeriodDialog = true },
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

            // Version label
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
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        SettingsScreen(
            state = state,
            validationEvents = MutableSharedFlow(),
            onNavigateBack = {},
            onUpdateDailyGoal = {},
            onUpdateCustomDailyGoalString = {},
            onUpdateUnit = {},
            onUpdateNotificationToggle = {},
            onUpdateFrequency = {},
            onUpdateStartAndEndTimes = { _, _ -> },
            onUpdateFastingState = {},
            onUpdateTheme = {},
            onUpdateLanguage = {},
        )
    }
}