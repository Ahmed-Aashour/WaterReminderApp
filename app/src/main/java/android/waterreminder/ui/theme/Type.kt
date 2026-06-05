package android.waterreminder.ui.theme

import android.waterreminder.R
import androidx.compose.material3.Typography
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AgbalumoFont = FontFamily(
    Font(resId = R.font.agbalumo_regular, weight = FontWeight.Normal)
)

object ErtawyTypography {
    val logoStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 64.sp)
    val titleStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 32.sp)
    val sectionStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 20.sp,
        lineHeight = 24.sp,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )
    val normalStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 14.sp)
    val smallStyle = TextStyle(fontFamily = AgbalumoFont, fontSize = 10.sp)
}

// Maps your custom definitions directly into Material3 structural presets
val Material3TypographyBridge = Typography(
    displayLarge = ErtawyTypography.logoStyle,
    headlineLarge = ErtawyTypography.titleStyle,
    titleLarge = ErtawyTypography.sectionStyle,
    bodyLarge = ErtawyTypography.normalStyle,
    labelSmall = ErtawyTypography.smallStyle
)