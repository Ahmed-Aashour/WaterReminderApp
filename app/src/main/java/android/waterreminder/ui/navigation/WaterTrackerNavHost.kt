package android.waterreminder.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.waterreminder.ui.dashboard.DashboardRoute
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text

@Composable
fun WaterTrackerNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        // Set the primary landing screen when the app opens
        startDestination = DashboardRouteDestination,
        modifier = modifier
    ) {
        // Dashboard Screen Graph Node
        composable<DashboardRouteDestination> {
            DashboardRoute(
                onNavigateToSettings = {
                    navController.navigate(SettingsRouteDestination)
                }
            )
        }

        // Settings Screen Graph Node Placeholder
        composable<SettingsRouteDestination> {
            // placeholder!
            SettingsRoutePlaceholder(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun SettingsRoutePlaceholder(onNavigateBack: () -> Unit) {
    // Temporary visual placeholder to verify navigation functions are working perfectly
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Button(onClick = onNavigateBack) {
            Text("Placeholder Settings Screen: Go Back")
        }
    }
}