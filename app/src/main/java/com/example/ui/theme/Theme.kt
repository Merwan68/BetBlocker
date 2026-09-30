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
    primary = ShieldEmerald400,
    onPrimary = ShieldNavy900,
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = ShieldEmerald300,
    secondary = ShieldCyan400,
    onSecondary = ShieldNavy900,
    secondaryContainer = Color(0xFF164E63),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = Color(0xFF818CF8),
    background = ShieldNavy900,
    onBackground = ShieldSlate100,
    surface = ShieldNavy800,
    onSurface = ShieldSlate100,
    surfaceVariant = ShieldNavy700,
    onSurfaceVariant = ShieldSlate400,
    outline = ShieldNavy600,
    error = ShieldRed400,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF059669),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = Color(0xFF0891B2),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCFFAFE),
    onSecondaryContainer = Color(0xFF155E75),
    tertiary = Color(0xFF4F46E5),
    background = Color(0xFFF8FAFC),
    onBackground = ShieldNavy900,
    surface = Color.White,
    onSurface = ShieldNavy900,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = ShieldSlate500,
    outline = Color(0xFFCBD5E1),
    error = ShieldRed500,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our signature security palette for consistency
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

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
