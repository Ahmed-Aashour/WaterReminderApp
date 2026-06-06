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
    viewModel: DashboardViewModel = hiltViewModel() // Injected via Hilt
) {
    // Collect state safely respecting lifecycle states (stops collecting when app is in background)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Local UI State tracking visibility of the dialog overlay
    var showCustomDialog by remember { mutableStateOf(false) }

    DashboardScreen(
        state = uiState,
        onAddWater = { amount -> viewModel.logWater(amount) },
        onCustomAddTrigger = { showCustomDialog = true }, // Toggle state open
        onDeleteLog = { log -> viewModel.deleteWaterLog(log) },
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )

    // Render overlay cleanly outside the structural Column layout thread
    if (showCustomDialog) {
        CustomWaterInputDialog(
            onDismiss = { showCustomDialog = false },
            onConfirm = { customAmount ->
                viewModel.logWater(customAmount)
                showCustomDialog = false // Close on success
            }
        )
    }
}