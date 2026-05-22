package com.example.gabsstudentstay.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    // backgrounds
    background = NearWhite,              // off white background
    surface = White,                     // white cards
    surfaceVariant = BlueGrey20,         // light blue grey placeholders

    // primary — main blue
    primary = Blue60,                    // #1976D2 vivid blue
    onPrimary = White,                   // white text on blue
    primaryContainer = Blue20,           // light blue container
    onPrimaryContainer = Blue80,         // dark blue text on light blue

    // secondary — lighter blue
    secondary = Blue40,                  // #42A5F5 lighter blue
    onSecondary = White,                 // white text on secondary
    secondaryContainer = Blue10,         // very light blue
    onSecondaryContainer = Blue80,       // dark blue text

    // text on backgrounds
    onBackground = Color(0xFF0D1B2A),    // near black for max contrast on white
    onSurface = Color(0xFF0D1B2A),       // near black on white cards
    onSurfaceVariant = Color(0xFF455A64),// medium grey for secondary text

    // outline
    outline = Color(0xFFB0BEC5),         // light grey outline

    // error
    error = ErrorRed,
    onError = White,
)

private val DarkColorScheme = darkColorScheme(
    // backgrounds
    background = NearBlack,             // #060E1A near black
    surface = DarkBlue20,               // #0F2040 dark blue cards
    surfaceVariant = DarkBlue30,        // #152B55 slightly lighter

    // primary — light blue for dark mode
    primary = LightBlue60,              // #64B5F6 light blue
    onPrimary = DarkBlue10,             // dark text on light blue
    primaryContainer = DarkBlue40,      // #1A3A6B container
    onPrimaryContainer = LightBlue80,   // very light blue text

    // secondary
    secondary = Blue40,                 // #42A5F5
    onSecondary = DarkBlue10,           // very dark on secondary
    secondaryContainer = DarkBlue30,    // dark blue container
    onSecondaryContainer = LightBlue80, // light blue text

    // text on backgrounds
    onBackground = Color(0xFFE8F4FD),   // very light blue white for max contrast
    onSurface = Color(0xFFE8F4FD),      // very light blue white on dark cards
    onSurfaceVariant = Color(0xFF90CAF9),// light blue for secondary text

    // outline
    outline = Color(0xFF37516F),        // dark blue outline

    // error
    error = ErrorRedDark,
    onError = Color(0xFF690014),
)

@Composable
fun GabsStudentStayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}