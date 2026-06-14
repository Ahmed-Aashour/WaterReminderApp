package android.waterreminder.ui.settings.components

import android.waterreminder.ui.core.components.ClickableTarget
import android.waterreminder.ui.core.components.SettingsItemRow
import android.waterreminder.ui.core.components.SettingsSectionHeader
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HydrationFrame(
    dailyGoal: Int,
    unit: String,
    onGoalClick: () -> Unit,
    onUnitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Hydration Target")

        SettingsItemRow(
            title = "Goal",
            description = "$dailyGoal ml",
            onClick = onGoalClick
        )
        SettingsItemRow(
            title = "Unit",
            description = if (unit == "ml") "Milliliters (ml)" else "Fluid Ounces (fl oz)",
            onClick = onUnitClick
        )
    }
}

@Composable
fun NotificationsFrame(
    isNotificationEnabled: Boolean,
    frequencyMinutes: Int,
    reminderWindow: String,
    isFastingMode: Boolean,
    onIntervalToggle: (Boolean) -> Unit,
    onFrequencyClick: () -> Unit,
    onWindowClick: () -> Unit,
    onFastingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Notifications")

        SettingsItemRow(
            title = "Scheduled Push Notifications",
            description = if (isNotificationEnabled) "Enabled" else "Disabled",
            controlSlot = {
                Switch(checked = isNotificationEnabled, onCheckedChange = onIntervalToggle)
            }
        )
        SettingsItemRow(
            title = "Frequency",
            description = "Every $frequencyMinutes minutes",
            onClick = onFrequencyClick
        )
        SettingsItemRow(
            title = "Period",
            description = reminderWindow,
            onClick = onWindowClick
        )
        SettingsItemRow(
            title = "Fasting Mode",
            description = if (isFastingMode) "Active" else "Inactive",
            controlSlot = {
                Switch(checked = isFastingMode, onCheckedChange = onFastingToggle)
            }
        )
    }
}

@Composable
fun PreferencesFrame(
    theme: String,
    language: String,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Preferences")

        SettingsItemRow(
            title = "Theme",
            description = theme,
            onClick = onThemeClick
        )
        SettingsItemRow(
            title = "Language",
            description = language,
            onClick = onLanguageClick
        )
    }
}

@Composable
fun LegalLinksFrame(
    onAboutClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onTermsClick: () -> Unit,
    onAcknowledgementsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        SettingsItemRow(
            title = "About",
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onAboutClick
        )
        SettingsItemRow(
            title = "Feedback",
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onFeedbackClick
        )
        SettingsItemRow(
            title = "Terms & Policies",
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onTermsClick
        )
        SettingsItemRow(
            title = "Acknowledgements",
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onAcknowledgementsClick
        )
    }
}