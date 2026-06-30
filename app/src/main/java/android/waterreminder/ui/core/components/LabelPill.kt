package android.waterreminder.ui.core.components

import android.waterreminder.data.entity.AppTheme
import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LabelPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
            .padding(horizontal = 14.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview(name = "LabelPill - Light Mode", showBackground = true)
@Composable
fun LabelPillCorePreview() {
    ErtawyTheme(appTheme = AppTheme.LIGHT) {
        Box(modifier = Modifier.padding(16.dp)) {
            LabelPill(text = "365 days hydrated!")
        }
    }
}

@Preview(name = "LabelPill - Dark Mode", showBackground = true)
@Composable
fun LabelPillCoreDarkPreview() {
    ErtawyTheme(appTheme = AppTheme.DARK) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            LabelPill(text = "Generic!")
        }
    }
}