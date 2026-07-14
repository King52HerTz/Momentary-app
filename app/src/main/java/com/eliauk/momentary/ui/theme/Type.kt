package com.eliauk.momentary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val InterfaceSans = FontFamily.SansSerif
private val JournalSerif = FontFamily.Serif

val MomentaryTypography = Typography(
    displaySmall = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 38.sp),
    headlineLarge = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.SemiBold, fontSize = 23.sp, lineHeight = 31.sp),
    titleLarge = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Medium, fontSize = 17.sp, lineHeight = 24.sp),
    titleSmall = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 22.sp),
    bodySmall = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 18.sp),
    labelSmall = TextStyle(fontFamily = InterfaceSans, fontWeight = FontWeight.Normal, fontSize = 11.sp, lineHeight = 16.sp),
)

val JournalBodyStyle = TextStyle(
    fontFamily = JournalSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 30.sp,
)
