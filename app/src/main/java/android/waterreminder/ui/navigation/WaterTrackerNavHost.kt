package android.waterreminder.ui.navigation

import android.waterreminder.ui.dashboard.DashboardRoute
import android.waterreminder.ui.settings.SettingsRoute
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        // TODO: fix navigation white screen stuck error when navigating back to settings when animation is incomplete
        // - Dashboard Screen Graph Node
        composable<DashboardRouteDestination> {
            DashboardRoute(
                onNavigateToSettings = {
                    navController.navigate(SettingsRouteDestination)
                }
            )
        }

        // - Settings Screen Graph Node Placeholder
        composable<SettingsRouteDestination> {
            SettingsRoute(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}