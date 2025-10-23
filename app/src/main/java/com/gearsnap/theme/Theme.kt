package com.gearsnap.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.foundation.isSystemInDarkTheme


// Couleurs — Outdoor
val GS_Green = Color(0xFF2E6B4A)   // vert nature
val GS_Blue  = Color(0xFF2E7BBE)   // bleu horizon
val GS_Oran  = Color(0xFFF47B20)   // accent aventure
val GS_NeutralBg = Color(0xFFF6F6F6)

private val Light = lightColorScheme(
    primary   = GS_Green,
    secondary = GS_Blue,
    tertiary  = GS_Oran,
    background = Color(0xFFFAFAFA),
    surface    = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A)
)

private val Dark = darkColorScheme(
    primary   = Color(0xFF58A379),
    secondary = Color(0xFF58A7F0),
    tertiary  = Color(0xFFFF9C54),
    background = Color(0xFF0B0E13),
    surface    = Color(0xFF12151C),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFFE2E8F0),
    onSurface = Color(0xFFE2E8F0)
)

// Typo simple (tu pourras remplacer par Poppins/RobotoSlab quand tu auras les .ttf)
val GSTypography = Typography(
    headlineMedium = TextStyle(fontSize = 24.sp),
    headlineSmall  = TextStyle(fontSize = 20.sp),
    titleMedium    = TextStyle(fontSize = 18.sp),
    bodyLarge      = TextStyle(fontSize = 16.sp),
    bodyMedium     = TextStyle(fontSize = 14.sp),
    labelMedium    = TextStyle(fontSize = 13.sp)
)

// Rayons & élévations
val GSShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small      = RoundedCornerShape(12.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun GearSnapTheme(useDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (useDark) Dark else Light,
        typography  = GSTypography,
        shapes      = GSShapes,
        content     = content
    )
}
