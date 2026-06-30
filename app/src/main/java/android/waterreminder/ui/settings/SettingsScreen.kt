package android.waterreminder.ui.settings

import android.content.res.Configuration
import android.waterreminder.BuildConfig
import android.waterreminder.R
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
import android.waterreminder.ui.core.components.Header
import android.waterreminder.ui.core.components.SquareIconButton
import android.waterreminder.ui.settings.components.*
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    validationEvents: Flow<String>,
    onNavigateBack: () -> Unit,
    onUpdateDailyGoal: (Int) -> Unit,
    onUpdateCustomDailyGoalString: (String) -> Unit,
    onUpdateUnit: (AppUnit) -> Unit,
    onUpdateNotificationToggle: (Boolean) -> Unit,
    onUpdateFrequency: (Int) -> Unit,
    onUpdateStartAndEndTimes: (LocalTime, LocalTime) -> Unit,
    onUpdateFastingState: (Boolean) -> Unit,
    onUpdateTheme: (AppTheme) -> Unit,
    onUpdateLanguage: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeDialog by remember { mutableStateOf<ActiveSettingsDialog>(ActiveSettingsDialog.None) }

    // Unified conditional rendering block
    when (activeDialog) {
        ActiveSettingsDialog.None -> {}
        ActiveSettingsDialog.DailyGoal -> {
            DailyGoalDialog(
                currentGoalMl = state.dailyGoalMl,
                currentUnit = state.unit,
                predefinedOptions = state.predefinedGoals,
                validationEvents = validationEvents,
                onDismiss = { activeDialog = ActiveSettingsDialog.None },
                onConfirm = { selectedGoal ->
                    onUpdateDailyGoal(selectedGoal)
                    activeDialog = ActiveSettingsDialog.None
                },
                onConfirmCustomString = { customString ->
                    onUpdateCustomDailyGoalString(customString)
                }
            )
        }
        ActiveSettingsDialog.Unit -> {
            UnitDialog(
                currentUnit = state.unit,
                onUnitSelected = { selectedUnit -> onUpdateUnit(selectedUnit) },
                onDismiss = { activeDialog = ActiveSettingsDialog.None }
            )
        }
        ActiveSettingsDialog.Frequency -> {
            FrequencyDialog(
                currentFrequency = state.frequency,
                frequencyOptions = state.supportedFrequencies,
                onFrequencySelected = { selectedMinutes ->
                    onUpdateFrequency(selectedMinutes)
                },
                onDismiss = { activeDialog = ActiveSettingsDialog.None }
            )
        }
        ActiveSettingsDialog.Period -> {
            ReminderTimesDialog(
                currentStartTime = state.startTime,
                currentEndTime = state.endTime,
                onDismiss = { activeDialog = ActiveSettingsDialog.None },
                onConfirm = { startTime, endTime ->
                    onUpdateStartAndEndTimes(startTime, endTime)
                }
            )
        }
        ActiveSettingsDialog.Theme -> {
            ThemeDialog(
                currentTheme = state.theme,
                onThemeSelected = { selectedTheme -> onUpdateTheme(selectedTheme) },
                onDismiss = { activeDialog = ActiveSettingsDialog.None }
            )
        }
        ActiveSettingsDialog.Language -> {
            LanguageDialog(
                currentLanguage = state.language,
                onLanguageSelected = { selectedLanguage -> onUpdateLanguage(selectedLanguage) },
                onDismiss = { activeDialog = ActiveSettingsDialog.None }
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Header(
                title = stringResource(R.string.settings_title),
                actionButton = {
                    SquareIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.content_description_navigate_back),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateBack
                    )
                }
            )

            HydrationFrame(
                dailyGoal = state.dailyGoalMl,
                unit = state.unit,
                onGoalClick = { activeDialog = ActiveSettingsDialog.DailyGoal },
                onUnitClick = { activeDialog = ActiveSettingsDialog.Unit }
            )

            NotificationsFrame(
                isNotificationEnabled = state.areNotificationsEnabled,
                frequencyMinutes = state.frequency,
                reminderWindow = "${state.activeStartTime} - ${state.activeEndTime}",
                isFastingMode = state.isFasting,
                onNotificationToggle = onUpdateNotificationToggle,
                onFrequencyClick = { activeDialog = ActiveSettingsDialog.Frequency },
                onWindowClick = { activeDialog = ActiveSettingsDialog.Period },
                onFastingToggle = onUpdateFastingState
            )

            PreferencesFrame(
                theme = state.theme,
                language = state.language,
                onThemeClick = { activeDialog = ActiveSettingsDialog.Theme },
                onLanguageClick = { activeDialog = ActiveSettingsDialog.Language }
            )

            LegalLinksFrame(
                onAboutClick = { },
                onFeedbackClick = { },
                onTermsClick = { },
                onAcknowledgementsClick = { }
            )

            // Version label
            Text(
                text = stringResource(R.string.settings_version_format, BuildConfig.VERSION_NAME),
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