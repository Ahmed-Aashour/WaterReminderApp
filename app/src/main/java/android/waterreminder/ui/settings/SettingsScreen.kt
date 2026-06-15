package android.waterreminder.ui.settings

import android.content.res.Configuration
import android.waterreminder.ui.core.components.Header
import android.waterreminder.ui.core.components.SquareIconButton
import android.waterreminder.ui.settings.components.DailyGoalDialog
import android.waterreminder.ui.settings.components.HydrationFrame
import android.waterreminder.ui.settings.components.LegalLinksFrame
import android.waterreminder.ui.settings.components.NotificationsFrame
import android.waterreminder.ui.settings.components.PreferencesFrame
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
    onUpdateMeasurementUnit: (String) -> Unit,
    onUpdateFastingState: (Boolean) -> Unit,
    onUpdateNotificationInterval: (Int) -> Unit,
    onUpdateReminderWindow: (String, String) -> Unit,
    onUpdateTheme: (String) -> Unit,
    onUpdateLanguage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showGoalDialog by remember { mutableStateOf(false) }

    if (showGoalDialog) {
        DailyGoalDialog(
            predefinedOptions = state.predefinedGoalOptions,
            currentGoalMl = state.dailyGoalMl,
            currentUnit = state.measurementUnit,
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
                unit = state.measurementUnit,
                onGoalClick = { showGoalDialog = true },
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
    @PreviewParameter(SettingsScreenStateProvider::class) state: SettingsUiState
) {
    ErtawyTheme {
        SettingsScreen(
            state = state,
            validationEvents = MutableSharedFlow(),
            onNavigateBack = {},
            onUpdateDailyGoal = {},
            onUpdateCustomDailyGoalString = {},
            onUpdateMeasurementUnit = {},
            onUpdateFastingState = {},
            onUpdateNotificationInterval = {},
            onUpdateReminderWindow = { _, _ -> },
            onUpdateTheme = {},
            onUpdateLanguage = {}
        )
    }
}