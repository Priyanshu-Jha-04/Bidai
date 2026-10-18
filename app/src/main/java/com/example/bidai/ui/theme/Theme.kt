package com.example.bidai.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//light only
private val LightColorScheme = lightColorScheme(
    primary = Brand,
    onPrimary = Color.White,
    primaryContainer = BrandTint,
    onPrimaryContainer = BrandDark,
    background = Color.White,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    onSurfaceVariant = Muted,
    surfaceVariant = Surface50,
    outline = Outline,
    outlineVariant = Outline,
    error = ErrorRed,
    errorContainer = ErrorTint,
    onErrorContainer = ErrorRed,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
)

@Composable
fun BidaiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
