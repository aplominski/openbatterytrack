package dev.aplominski.openbatterytrack

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

private val Background = Color(0xFF121417)
private val Surface = Color(0xFF121417)
private val TextPrimary = Color(0xFFE8EAED)
private val TextSecondary = Color(0xFF9AA0A6)
private val Green = Color(0xFF22C55E)
private val Red = Color(0xFFEF4444)

val InterFamily = FontFamily.SansSerif

private val DarkScheme = darkColorScheme(
    background = Background,
    surface = Surface,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    primary = Green,
    error = Red
)

@Composable
fun OpenBatteryTrackerTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, content = content)
}
