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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// GearSnap Outdoor Adventure Theme — Exact color specifications
val GS_ForestGreen = Color(0xFF0F4C2E)        // Primary Forest Green
val GS_ForestGreenDark = Color(0xFF0A3620)    // Dark Forest Green
val GS_ForestGreenLight = Color(0xFF1A5C3A)   // Light Forest Green
val GS_WarmOrange = Color(0xFFF39A2D)         // Primary Warm Orange
val GS_WarmOrangeLight = Color(0xFFF5B568)    // Light Warm Orange
val GS_SoftBeige = Color(0xFFF7F3E8)          // Main Background
val GS_SearchBg = Color(0xFFEFEADD)           // Search Background
val GS_TitleGreen = Color(0xFF0A2A18)         // Title Text
val GS_BodyGray = Color(0xFF4A4A4A)           // Body Text
val GS_CardBg = Color(0xFFFFFFFF)             // Card Background
val GS_SurfaceVariant = Color(0xFFF0EBE0)     // Surface Variant
val GS_NavBg = Color(0xFF0F4C2E)              // Navigation Background
val GS_NavActive = Color(0xFFF39A2D)          // Navigation Active
val GS_NavInactive = Color(0xFFF7F3E8)        // Navigation Inactive

val GS_NeutralBg = GS_SoftBeige

private val Light = lightColorScheme(
    primary = GS_ForestGreen,
    onPrimary = Color.White,
    primaryContainer = GS_ForestGreenLight,
    onPrimaryContainer = Color.White,
    secondary = GS_WarmOrange,
    onSecondary = Color.White,
    secondaryContainer = GS_WarmOrangeLight,
    onSecondaryContainer = GS_ForestGreenDark,
    tertiary = GS_WarmOrange,
    onTertiary = Color.White,
    tertiaryContainer = GS_WarmOrangeLight,
    onTertiaryContainer = GS_ForestGreenDark,
    background = GS_SoftBeige,
    onBackground = GS_TitleGreen,
    surface = GS_CardBg,
    onSurface = GS_BodyGray,
    surfaceVariant = GS_SurfaceVariant,
    onSurfaceVariant = GS_BodyGray,
    outline = Color(0xFFB4AA9A),
    outlineVariant = Color(0xFFD9CFC0),
    scrim = Color(0xFF000000)
)

private val Dark = darkColorScheme(
    primary = Color(0xFF81C29B),
    onPrimary = Color(0xFF0E271B),
    primaryContainer = Color(0xFF123324),
    onPrimaryContainer = Color(0xFFCDEAD8),
    secondary = Color(0xFF71C1A6),
    onSecondary = Color(0xFF00382A),
    secondaryContainer = Color(0xFF004F3C),
    onSecondaryContainer = Color(0xFFB2EFD6),
    tertiary = Color(0xFFFFB874),
    onTertiary = Color(0xFF3D2000),
    tertiaryContainer = Color(0xFF5A3300),
    onTertiaryContainer = Color(0xFFFFDCC0),
    background = Color(0xFF0F1411),
    onBackground = Color(0xFFE4E0DA),
    surface = Color(0xFF171C18),
    onSurface = Color(0xFFE4E0DA),
    surfaceVariant = Color(0xFF424940),
    onSurfaceVariant = Color(0xFFC4CABB),
    outline = Color(0xFF8E9387),
    outlineVariant = Color(0xFF2C332A),
    scrim = Color(0xFF000000)
)

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
    MaterialTheme(
        colorScheme = if (useDark) Dark else Light,
        typography = GSTypography,
        shapes = GSShapes,
        content = content
    )
}
