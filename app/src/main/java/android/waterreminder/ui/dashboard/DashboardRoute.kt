package android.waterreminder.ui.dashboard

import android.waterreminder.ui.dashboard.components.ClearHistoryConfirmationDialog
import android.waterreminder.ui.dashboard.components.CustomWaterInputDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardRoute(
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Dialog visibility triggers
    var showCustomDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    val dashboardState = (uiState as? DashboardUiState.Success)?.data

    when (val state = uiState) {
        is DashboardUiState.Loading -> {
            // TODO: Design a professional loading shimmer or circular indicator skeleton screen
        }
        is DashboardUiState.Success -> {
            DashboardScreen(
                state = state.data,
                onAddWater = { amount -> viewModel.logWater(amount) },
                onCustomAddTrigger = { showCustomDialog = true },
                onDeleteLog = { log -> viewModel.deleteWaterLog(log) },
                onClearAllHistoryTrigger = { showClearHistoryDialog = true },
                onNavigateToSettings = onNavigateToSettings,
                modifier = modifier
            )
        }
    }

    // Overlay Zone: Custom Intake Dialog
    if (showCustomDialog) {
        dashboardState?.let { state ->
            CustomWaterInputDialog(
                currentUnit = state.drinkButtons.unit,
                validationEvents = viewModel.validationErrorChannel,
                onDismiss = { showCustomDialog = false },
                onConfirmCustomString = { customString ->
                    viewModel.addCustomWaterPresetAndLog(
                        inputString = customString,
                        unit = state.drinkButtons.unit,
                        onSuccess = { showCustomDialog = false }
                    )
                }
            )
        }
    }

    // Overlay Zone: Clear History Confirmation Dialog
    if (showClearHistoryDialog) {
        ClearHistoryConfirmationDialog(
            onDismiss = { showClearHistoryDialog = false },
            onConfirm = {
                viewModel.clearAllWaterHistory()
                showClearHistoryDialog = false
            }
        )
    }
}
