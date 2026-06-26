package android.waterreminder.ui.settings.components

import android.waterreminder.R
import android.waterreminder.data.entity.AppLanguage
import android.waterreminder.data.entity.AppTheme
import android.waterreminder.data.entity.AppUnit
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun HydrationFrame(
    dailyGoal: Int,
    unit: AppUnit,
    onGoalClick: () -> Unit,
    onUnitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SettingsSectionHeader(title = stringResource(R.string.settings_hydration_title))

        SettingsItemRow(
            title = stringResource(R.string.goal_row_title),
            description = "$dailyGoal ml",
            onClick = onGoalClick
        )
        SettingsItemRow(
            title = stringResource(R.string.unit_row_title),
            description = stringResource(
                id = if (unit == AppUnit.ML) R.string.unit_name_ml else R.string.unit_name_oz
            ),
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
        SettingsSectionHeader(title = stringResource(R.string.settings_notifications_title))

        SettingsItemRow(
            title = stringResource(R.string.push_notifications_row_title),
            description = stringResource(
                id = if (isNotificationEnabled) R.string.state_enabled else R.string.state_disabled
            ),
            controlSlot = {
                Switch(checked = isNotificationEnabled, onCheckedChange = onNotificationToggle)
            }
        )
        SettingsItemRow(
            title = stringResource(R.string.frequency_row_title),
            description = pluralStringResource(
                id = R.plurals.frequency_minutes_format,
                count = frequencyMinutes,
                frequencyMinutes
            ),
            onClick = if (isNotificationEnabled) onFrequencyClick else null,
            modifier = Modifier.alpha(configurationAlpha)
        )
        SettingsItemRow(
            title = stringResource(R.string.period_row_title),
            description = reminderWindow,
            onClick = if (isNotificationEnabled) onWindowClick else null,
            modifier = Modifier.alpha(configurationAlpha)
        )
        SettingsItemRow(
            title = stringResource(R.string.fasting_mode_row_title),
            description = stringResource(
                id = if (isFastingMode) R.string.state_active else R.string.state_inactive
            ),
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
            title = stringResource(R.string.about_row_title),
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onAboutClick
        )
        SettingsItemRow(
            title = stringResource(R.string.feedback_row_title),
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onFeedbackClick
        )
        SettingsItemRow(
            title = stringResource(R.string.terms_row_title),
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onTermsClick
        )
        SettingsItemRow(
            title = stringResource(R.string.acknowledgements_row_title),
            clickableTarget = ClickableTarget.TextOnly,
            onClick = onAcknowledgementsClick
        )
    }
}