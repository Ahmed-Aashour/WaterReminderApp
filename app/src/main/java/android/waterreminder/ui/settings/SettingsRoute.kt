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
    viewModel: SettingsViewModel = hiltViewModel()
) {
    // Collect the user preference state safely, pausing emissions when the app goes background
    val settingsState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        state = settingsState,
        validationEvents = viewModel.validationErrorChannel,
        onNavigateBack = onNavigateBack,
        onUpdateDailyGoal = { amountMl -> viewModel.updateDailyGoal(amountMl) },
        onUpdateCustomDailyGoalString = { amountMl -> viewModel.updateCustomDailyGoalString(amountMl) },
        onUpdateUnit = { unit -> viewModel.updateUnit(unit) },
        onUpdateNotificationToggle = { isEnabled -> viewModel.updateNotificationToggle(isEnabled) },
        onUpdateFrequency = { mins -> viewModel.updateFrequency(mins) },
        onUpdateStartAndEndTimes = { start, end -> viewModel.updateStartAndEndTimes(start, end) },
        onUpdateFastingState = { isFasting -> viewModel.updateFastingState(isFasting) },
        onUpdateTheme = { themeStr -> viewModel.updateTheme(themeStr) },
        onUpdateLanguage = { langStr -> viewModel.updateLanguage(langStr) },
        modifier = modifier,
    )
}