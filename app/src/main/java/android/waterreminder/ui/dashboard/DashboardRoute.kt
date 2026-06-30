package android.waterreminder.ui.dashboard

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
    var showCustomDialog by remember { mutableStateOf(false) }
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
                onNavigateToSettings = onNavigateToSettings,
                modifier = modifier
            )
        }
    }

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
}
