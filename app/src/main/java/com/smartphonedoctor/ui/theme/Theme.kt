package com.smartphonedoctor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF00BCD4),
    secondary = androidx.compose.ui.graphics.Color(0xFF4DB6AC),
    tertiary = androidx.compose.ui.graphics.Color(0xFF80CBC4)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF00838F),
    secondary = androidx.compose.ui.graphics.Color(0xFF00695C),
    tertiary = androidx.compose.ui.graphics.Color(0xFF004D40)
)

@Composable
fun SmartPhoneDoctorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
