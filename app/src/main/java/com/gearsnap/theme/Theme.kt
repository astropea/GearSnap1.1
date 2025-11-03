package com.gearsnap.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.gearsnap.R

val GSTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
        fontSize = 34.sp,
        lineHeight = 38.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)

val GSShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(36.dp)
)

@Composable
fun GearSnapTheme(useDark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val context = LocalContext.current

    val lightColorScheme = lightColorScheme(
        primary = Color(ContextCompat.getColor(context, R.color.gs_forest_green)),
        onPrimary = Color(ContextCompat.getColor(context, R.color.white)),
        primaryContainer = Color(ContextCompat.getColor(context, R.color.gs_forest_green_light)),
        onPrimaryContainer = Color(ContextCompat.getColor(context, R.color.white)),
        secondary = Color(ContextCompat.getColor(context, R.color.gs_warm_orange)),
        onSecondary = Color(ContextCompat.getColor(context, R.color.white)),
        secondaryContainer = Color(ContextCompat.getColor(context, R.color.gs_warm_orange_light)),
        onSecondaryContainer = Color(ContextCompat.getColor(context, R.color.gs_forest_green_dark)),
        tertiary = Color(ContextCompat.getColor(context, R.color.gs_warm_orange)),
        onTertiary = Color(ContextCompat.getColor(context, R.color.white)),
        tertiaryContainer = Color(ContextCompat.getColor(context, R.color.gs_warm_orange_light)),
        onTertiaryContainer = Color(ContextCompat.getColor(context, R.color.gs_forest_green_dark)),
        background = Color(ContextCompat.getColor(context, R.color.gs_soft_beige)),
        onBackground = Color(ContextCompat.getColor(context, R.color.gs_title_green)),
        surface = Color(ContextCompat.getColor(context, R.color.gs_card_bg)),
        onSurface = Color(ContextCompat.getColor(context, R.color.gs_body_gray)),
        surfaceVariant = Color(ContextCompat.getColor(context, R.color.gs_surface_variant)),
        onSurfaceVariant = Color(ContextCompat.getColor(context, R.color.gs_body_gray)),
        outline = Color(0xFFB4AA9A),
        outlineVariant = Color(0xFFD9CFC0),
        scrim = Color(0xFF000000)
    )

    val darkColorScheme = darkColorScheme(
        primary = Color(0xFF000000),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFF000000),
        onPrimaryContainer = Color(0xFF444444),
        secondary = Color(0xFF000000),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFF000000),
        onSecondaryContainer = Color(0xFFFFFFFF),
        tertiary = Color(0xFF444444),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFF444444),
        onTertiaryContainer = Color(0xFFFFFFFF),
        background = Color(0xFF000000),
        onBackground = Color(0xFFFFFFFF),
        surface = Color(0xFF444444),
        onSurface = Color(0xFFFFFFFF),
        surfaceVariant = Color(0xFF121212),
        onSurfaceVariant = Color(0xFFE0E0E0),
        outline = Color(0xFF444444),
        outlineVariant = Color(0xFF222222),
        scrim = Color(0xFF000000)
    )

    MaterialTheme(
        colorScheme = if (useDark) darkColorScheme else lightColorScheme,
        typography = GSTypography,
        shapes = GSShapes,
        content = content
    )
}
