package android.waterreminder.ui.core.components

import android.waterreminder.ui.theme.ErtawyTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .wrapContentWidth() // Instructs the box to wrap tightly around its contents
            .height(30.dp)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 14.dp), // Safe edge padding to protect text layout boundaries
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 1 // Keeps the pill text strictly on a single line
        )
    }
}

@Preview(name = "LabelPill - Light Mode", showBackground = true)
@Composable
fun LabelPillCorePreview() {
    ErtawyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            LabelPill(text = "365 days hydrated!")
        }
    }
}

@Preview(name = "LabelPill - Dark Mode", showBackground = true)
@Composable
fun LabelPillCoreDarkPreview() {
    ErtawyTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            LabelPill(text = "Generic!")
        }
    }
}