package com.example.netguardlite.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF1E5EFF),
    tertiary = Color(0xFF1B8A5A)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8FB0FF),
    tertiary = Color(0xFF6FD8A5)
)

@Composable
fun NetGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
