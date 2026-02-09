package com.example.pawelierapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 42.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.Default, fontSize = 16.sp, lineHeight = 20.sp)
)