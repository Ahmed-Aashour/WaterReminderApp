package android.waterreminder.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- LIGHT COLOR SCHEME ---
private val LightColorScheme = lightColorScheme(
    primary = WaterPrimary,
    onPrimary = WaterWhite,
    primaryContainer = WaterLight,
    onPrimaryContainer = WaterDark,
    surface = WaterWhite,
    onSurface = WaterDark,
    surfaceVariant = WaterLight,
    onSurfaceVariant = WaterDark,
    background = WaterWhite,
    onBackground = WaterDark
)

// --- DARK COLOR SCHEME ---
private val DarkColorScheme = darkColorScheme(
    primary = WaterLight,            // Flipped to Light blue so text remains highly readable on dark backgrounds
    onPrimary = WaterDark,
    primaryContainer = WaterDark,
    onPrimaryContainer = WaterPrimary,
    surface = WaterSurfaceDark,
    onSurface = WaterOnSurfaceDark,
    surfaceVariant = WaterSurfaceDark,
    onSurfaceVariant = WaterLight,
    background = WaterBackgroundDark,
    onBackground = WaterOnSurfaceDark
)

@Composable
fun ErtawyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    // Set the Android system status bar color to match the theme context smoothly
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // We link your typography structure cleanly into the composition lifecycle here
        typography = Material3TypographyBridge,
        content = content
    )
}