package android.waterreminder.ui.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

    DashboardScreen(
        state = uiState,
        onAddWater = { amount -> viewModel.logWater(amount) },
        onCustomAddTrigger = {
            // TODO: We will hook up the custom input dialog trigger here later!
        },
        onNavigateToSettings = onNavigateToSettings,
        modifier = modifier
    )
}