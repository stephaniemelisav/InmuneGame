package com.example.tpjuego.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.tpjuego.R

// Tipografías del Design System (sección 3). Licencia OFL, ver carpeta licencias/.
val Staatliches = FontFamily(
    Font(R.font.staatliches_regular, FontWeight.Normal)
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_semibold, FontWeight.SemiBold),
    Font(R.font.inter_bold, FontWeight.Bold)
)

// Staatliches: textos cortos y grandes. Inter: todo lo que tenga más de una línea.
// Ningún texto baja de 16 sp.
val Typography = Typography(
    // Título de la app: ¿MITO O REALIDAD?
    displayLarge = TextStyle(fontFamily = Staatliches, fontSize = 68.sp, lineHeight = 68.sp),
    // Título de pantalla: ¿VERDADERO O FALSO? / ¡RESPUESTA CORRECTA!
    headlineLarge = TextStyle(fontFamily = Staatliches, fontSize = 40.sp, lineHeight = 44.sp),
    // Título de las tarjetas del menú de juegos
    headlineMedium = TextStyle(fontFamily = Staatliches, fontSize = 28.sp, lineHeight = 32.sp),
    // Afirmación a evaluar
    headlineSmall = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, lineHeight = 30.sp
    ),
    // Subtítulo
    titleMedium = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp
    ),
    // Etiquetas: JUEGO DE PREGUNTAS / ES UN MITO · FALSO
    titleSmall = TextStyle(
        fontFamily = Staatliches, fontSize = 18.sp, lineHeight = 22.sp, letterSpacing = 1.sp
    ),
    // Indicador de progreso: PREGUNTA 1 DE 5
    labelMedium = TextStyle(
        fontFamily = Staatliches, fontSize = 16.sp, lineHeight = 20.sp, letterSpacing = 1.sp
    ),
    // Texto de botones
    labelLarge = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Bold, fontSize = 18.sp, lineHeight = 24.sp
    ),
    // Explicaciones y cuerpo
    bodyLarge = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 18.sp, lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp
    )
)
