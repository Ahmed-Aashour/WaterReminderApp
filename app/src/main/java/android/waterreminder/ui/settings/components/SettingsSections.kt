package android.waterreminder.ui.settings.components

import android.waterreminder.R
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.ui.core.components.ClickableTarget
import android.waterreminder.ui.core.components.SettingsItemRow
import android.waterreminder.ui.core.components.SettingsSectionHeader
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
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
    onNotificationToggle: (Boolean) -> Unit,
    onFrequencyClick: () -> Unit,
    onWindowClick: () -> Unit,
    onFastingToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val configurationAlpha = if (isNotificationEnabled) 1.0f else 0.45f

    Column(modifier = modifier) {
        SettingsSectionHeader(title = "Notifications")

        SettingsItemRow(
            title = "Scheduled Push Notifications",
            description = if (isNotificationEnabled) "Enabled" else "Disabled",
            controlSlot = {
                Switch(checked = isNotificationEnabled, onCheckedChange = onNotificationToggle)
            }
        )
        SettingsItemRow(
            title = "Frequency",
            description = "Every $frequencyMinutes minutes",
            onClick = if (isNotificationEnabled) onFrequencyClick else null,
            modifier = Modifier.alpha(configurationAlpha)
        )
        SettingsItemRow(
            title = "Period",
            description = reminderWindow,
            onClick = if (isNotificationEnabled) onWindowClick else null,
            modifier = Modifier.alpha(configurationAlpha)
        )
        SettingsItemRow(
            title = "Fasting Mode",
            description = if (isFastingMode) "Active" else "Inactive",
            controlSlot = {
                Switch(
                    checked = isFastingMode,
                    onCheckedChange = onFastingToggle,
                    enabled = isNotificationEnabled,
                )
            },
            modifier = Modifier.alpha(configurationAlpha)
        )
    }
}

@Composable
fun PreferencesFrame(
    theme: AppTheme,
    language: AppLanguage,
    onThemeClick: () -> Unit,
    onLanguageClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(
            title = stringResource(R.string.settings_preferences_title)
        )

        SettingsItemRow(
            title = stringResource(R.string.theme_row_title),
            description = stringResource(id = theme.getDisplayLabelRes()),
            onClick = onThemeClick
        )
        SettingsItemRow(
            title = stringResource(R.string.language_row_title),
            description = stringResource(id = language.getDisplayLabelRes()),
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