package com.example.tpjuego.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta del Design System (design-system.md, sección 2).
// Todos los tonos ya están ajustados para cumplir el contraste WCAG.

val Turquesa = Color(0xFF0A808A)
val AzulNoche = Color(0xFF1B2A4A)
val Amarillo = Color(0xFFFFC53D)
val Blanco = Color(0xFFFFFFFF)

// Botones de respuesta
val VerdeVerdadero = Color(0xFF1B7F4A)
val RojoFalso = Color(0xFFD03A3F)

// Fondos de feedback
val VerdeEstado = Color(0xFF0E7F6B)
val VerdeEstadoOscuro = Color(0xFF0B3D3A)
val NaranjaEstado = Color(0xFFC8502E)
val NaranjaEstadoOscuro = Color(0xFF4A1730)

// Elementos secundarios
val BlancoTranslucido = Color(0x33FFFFFF) // blanco al 20 %
val BlancoBorde = Color(0x99FFFFFF)       // blanco al 60 %, borde del botón secundario

// Degradés de fondo
val DegradeGeneral = listOf(Turquesa, AzulNoche)
val DegradeAcierto = listOf(VerdeEstado, VerdeEstadoOscuro)
val DegradeError = listOf(NaranjaEstado, NaranjaEstadoOscuro)
