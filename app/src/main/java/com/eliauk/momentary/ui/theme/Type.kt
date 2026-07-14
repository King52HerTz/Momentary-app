package com.eliauk.momentary.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val JournalSerif = FontFamily.Serif

val MomentaryTypography = Typography(
    displaySmall = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.SemiBold, fontSize = 36.sp, lineHeight = 44.sp),
    headlineLarge = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.SemiBold, fontSize = 30.sp, lineHeight = 38.sp),
    headlineMedium = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 30.sp),
    titleMedium = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.Medium, fontSize = 18.sp, lineHeight = 26.sp),
    bodyLarge = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.Normal, fontSize = 17.sp, lineHeight = 29.sp),
    bodyMedium = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 24.sp),
    labelLarge = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.Medium, fontSize = 15.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = JournalSerif, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 18.sp),
)
