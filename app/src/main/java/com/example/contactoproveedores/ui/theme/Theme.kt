package com.example.contactoproveedores.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = VerdeContacto, onPrimary = Color.White,
    background = FondoPanel, surface = Color.White,
    onSurface = TextoPrincipal, onBackground = TextoPrincipal,
    onSurfaceVariant = TextoSecundario, surfaceVariant = Color(0xFFF0F3F5),
    outline = Color(0xFFAAB4BC), outlineVariant = BordePanel,
    secondaryContainer = VerdeSuave, onSecondaryContainer = Color(0xFF087F5B)
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
