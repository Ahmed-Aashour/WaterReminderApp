package android.waterreminder.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

enum class ClickableTarget {
    Row,      // The whole row intercepts clicks (Default - for interactive configuration frames)
    TextOnly  // Only the text boundary intercepts clicks (For clean legal links)
}

@Composable
fun SettingsItemRow(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    clickableTarget: ClickableTarget = ClickableTarget.Row,
    controlSlot: @Composable (() -> Unit)? = null
) {
    val isRowClickable = onClick != null && clickableTarget == ClickableTarget.Row
    val isTextClickable = onClick != null && clickableTarget == ClickableTarget.TextOnly

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isRowClickable) Modifier.clickable { onClick.invoke() } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (isTextClickable) {
                        Modifier
                            .wrapContentWidth(Alignment.Start)
                            .clickable { onClick?.invoke() }
                    } else Modifier
                )
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                )
            }
        }

        controlSlot?.invoke()
    }
}

@Composable
fun SettingsSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
            .fillMaxWidth()
    )
}