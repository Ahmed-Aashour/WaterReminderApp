package android.waterreminder.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.waterreminder.R

// Load Custom Typography Font File
val AgbalumoFont = FontFamily(
    Font(resId = R.font.agbalumo_regular, weight = FontWeight.Normal)
)

// Isolated Typography Stylesheet
object ErtawyTypography {
    val titleStyle = TextStyle(
        fontFamily = AgbalumoFont,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp
    )
}