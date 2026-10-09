package com.example.contactoproveedores.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF0F766E), onPrimary = Color.White,
    background = Color(0xFFF8FAFC), surface = Color.White,
    onSurface = Color(0xFF0F172A), onBackground = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B), surfaceVariant = Color(0xFFF1F5F9),
    outline = Color(0xFFCBD5E1), outlineVariant = Color(0xFFE2E8F0),
    secondaryContainer = Color(0xFFE6F5EF), onSecondaryContainer = Color(0xFF087F5B)
)
private val Dark = darkColorScheme(
    primary = Color(0xFF5EEAD4), background = Color(0xFF0B1220),
    surface = Color(0xFF111C2E), onSurface = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFF273449)
)
@Composable
fun ContactoProveedoresTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, typography = Typography, content = content)
}
