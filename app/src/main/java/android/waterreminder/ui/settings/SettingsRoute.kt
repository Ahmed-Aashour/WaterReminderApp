package android.waterreminder.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel() // 🌟 Instantiated automatically via Hilt
) {
    // Collect the user preference state safely, pausing emissions when the app goes background
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        state = settingsState,
        onNavigateBack = onNavigateBack,
        onUpdateDailyGoal = { amountMl -> viewModel.updateDailyGoal(amountMl) },
        onUpdateMeasurementUnit = { unit -> viewModel.updateMeasurementUnit(unit) },
        onUpdateFastingState = { isFasting -> viewModel.updateFastingState(isFasting) },
        onUpdateNotificationInterval = { mins -> viewModel.updateNotificationInterval(mins) },
        onUpdateTheme = { themeStr -> viewModel.updateTheme(themeStr) },
        onUpdateLanguage = { langStr -> viewModel.updateLanguage(langStr) },
        onUpdateReminderWindow = { start, end -> viewModel.updateReminderWindow(start, end) },
        modifier = modifier
    )
}