package android.waterreminder.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

//@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
//@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Light Mode",
    group = "Themes",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    group = "Themes",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class ThemePreviews