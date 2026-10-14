package com.example.tpjuego.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// La app usa siempre fondo oscuro con texto claro (ver justificación de la paleta),
// así que hay un único esquema de colores, sin modo claro/oscuro.
private val InmuneColorScheme = darkColorScheme(
    primary = Amarillo,
    onPrimary = AzulNoche,
    secondary = Turquesa,
    onSecondary = Blanco,
    background = AzulNoche,
    onBackground = Blanco,
    surface = Blanco,
    onSurface = AzulNoche,
    error = RojoFalso,
    onError = Blanco
)

@Composable
fun TPJuegoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = InmuneColorScheme,
        typography = Typography,
        content = content
    )
}
