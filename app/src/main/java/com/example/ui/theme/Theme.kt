package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color.Black,
    primaryContainer = SchoolBlueDark,
    onPrimaryContainer = Color.White,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText
)

private val LightColorScheme = lightColorScheme(
    primary = SchoolBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SchoolBlueContainer,
    onPrimaryContainer = SchoolBlueDark,
    secondary = SchoolTeal,
    onSecondary = Color.White,
    secondaryContainer = SchoolTealLight,
    onSecondaryContainer = Color(0xFF004D40),
    background = SlateBg,
    surface = Color.White,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextMuted,
    outline = CardBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep tailored school colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
