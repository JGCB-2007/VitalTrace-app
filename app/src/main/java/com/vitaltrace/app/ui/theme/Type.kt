package com.vitaltrace.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.vitaltrace.app.R

/** Sora is bundled with the app so typography is consistent when offline. */
val SoraFontFamily = FontFamily(
    Font(R.font.sora_variable, FontWeight.Normal),
    Font(R.font.sora_variable, FontWeight.Medium),
    Font(R.font.sora_variable, FontWeight.SemiBold),
    Font(R.font.sora_variable, FontWeight.Bold),
    Font(R.font.sora_variable, FontWeight.ExtraBold)
)

private val BaseTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SoraFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.7.sp
    )
)

/** Every Material 3 text role uses Sora, including roles that keep default sizing. */
val Typography = Typography(
    displayLarge = BaseTypography.displayLarge.copy(fontFamily = SoraFontFamily),
    displayMedium = BaseTypography.displayMedium.copy(fontFamily = SoraFontFamily),
    displaySmall = BaseTypography.displaySmall.copy(fontFamily = SoraFontFamily),
    headlineLarge = BaseTypography.headlineLarge.copy(fontFamily = SoraFontFamily),
    headlineMedium = BaseTypography.headlineMedium.copy(fontFamily = SoraFontFamily),
    headlineSmall = BaseTypography.headlineSmall.copy(fontFamily = SoraFontFamily),
    titleLarge = BaseTypography.titleLarge.copy(fontFamily = SoraFontFamily),
    titleMedium = BaseTypography.titleMedium.copy(fontFamily = SoraFontFamily),
    titleSmall = BaseTypography.titleSmall.copy(fontFamily = SoraFontFamily),
    bodyLarge = BaseTypography.bodyLarge.copy(fontFamily = SoraFontFamily),
    bodyMedium = BaseTypography.bodyMedium.copy(fontFamily = SoraFontFamily),
    bodySmall = BaseTypography.bodySmall.copy(fontFamily = SoraFontFamily),
    labelLarge = BaseTypography.labelLarge.copy(fontFamily = SoraFontFamily),
    labelMedium = BaseTypography.labelMedium.copy(fontFamily = SoraFontFamily),
    labelSmall = BaseTypography.labelSmall.copy(fontFamily = SoraFontFamily)
)
