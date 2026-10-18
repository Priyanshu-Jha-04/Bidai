package com.example.bidai.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.bidai.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
)

private fun style(weight: FontWeight, size: TextUnit, lineHeight: TextUnit) =
    TextStyle(fontFamily = Poppins, fontWeight = weight, fontSize = size, lineHeight = lineHeight)

private val base = Typography()

//typography
val Typography = base.copy(
    displayLarge = base.displayLarge.copy(fontFamily = Poppins),
    displayMedium = base.displayMedium.copy(fontFamily = Poppins),
    displaySmall = base.displaySmall.copy(fontFamily = Poppins),
    titleSmall = base.titleSmall.copy(fontFamily = Poppins),
    bodySmall = base.bodySmall.copy(fontFamily = Poppins),
    labelSmall = base.labelSmall.copy(fontFamily = Poppins),
    headlineSmall = base.headlineSmall.copy(fontFamily = Poppins),
    headlineLarge = style(FontWeight.SemiBold, 28.sp, 36.sp),
    headlineMedium = style(FontWeight.SemiBold, 22.sp, 30.sp),
    titleLarge = style(FontWeight.SemiBold, 19.sp, 28.sp),
    titleMedium = style(FontWeight.SemiBold, 15.sp, 22.sp),
    bodyLarge = style(FontWeight.Normal, 15.sp, 24.sp),
    bodyMedium = style(FontWeight.Normal, 13.sp, 20.sp),
    labelLarge = style(FontWeight.Medium, 14.sp, 20.sp),
    labelMedium = style(FontWeight.Medium, 12.sp, 16.sp),
)
