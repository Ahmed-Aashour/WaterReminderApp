package android.waterreminder.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import android.waterreminder.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.style.BaselineShift

val AgbalumoFont = FontFamily(
    Font(resId = R.font.agbalumo_regular, weight = FontWeight.Normal)
)

object ErtawyTypography {
    val logoStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 64.sp)
    val titleStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 32.sp)
    val sectionStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 20.sp)
    val normalStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 14.sp)
    val smallStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 10.sp)
}