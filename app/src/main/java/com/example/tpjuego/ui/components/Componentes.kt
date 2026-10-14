package com.example.tpjuego.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.BlancoBorde
import com.example.tpjuego.ui.theme.BlancoTranslucido
import com.example.tpjuego.ui.theme.DegradeGeneral

// Componentes del Design System (secciones 4 y 5). Medidas en dp, tomando 1 px del Figma = 1 dp.

/** Fondo con degradé y márgenes estándar: 24 dp a los costados, 32 dp abajo. */
@Composable
fun PantallaDegradado(
    modifier: Modifier = Modifier,
    colores: List<Color> = DegradeGeneral,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colores))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 32.dp),
            content = contenido
        )
    }
}

/** Botón amarillo: la acción principal de la pantalla. Uno solo por pantalla. */
@Composable
fun BotonPrimario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BotonRelleno(texto, onClick, fondo = Amarillo, colorTexto = AzulNoche, modifier = modifier)
}

/** Botones Verdadero / Falso. */
@Composable
fun BotonOpcion(texto: String, fondo: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    BotonRelleno(texto, onClick, fondo = fondo, colorTexto = Blanco, modifier = modifier)
}

@Composable
private fun BotonRelleno(
    texto: String,
    onClick: () -> Unit,
    fondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = fondo, contentColor = colorTexto),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Botón sin relleno con borde blanco: "Volver al inicio", "Volver al menú". */
@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, BlancoBorde),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Blanco)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

/** Tarjeta blanca: la "zona de lectura" con el mayor contraste de la pantalla. */
@Composable
fun Tarjeta(
    modifier: Modifier = Modifier,
    alineacion: Alignment.Horizontal = Alignment.Start,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Blanco,
        contentColor = AzulNoche,
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = alineacion,
            content = contenido
        )
    }
}

/**
 * "PREGUNTA X DE 5" con barra amarilla sobre riel blanco al 20 %,
 * y a la derecha el contador de aciertos (✓) y errores (✕).
 */
@Composable
fun BarraProgreso(
    actual: Int,
    total: Int,
    aciertos: Int,
    errores: Int,
    modifier: Modifier = Modifier
) {
    val progreso by animateFloatAsState(
        targetValue = actual.toFloat() / total,
        label = "progreso"
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "PREGUNTA $actual DE $total",
                style = MaterialTheme.typography.labelMedium,
                color = Blanco,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "✓ $aciertos   ✕ $errores",
                style = MaterialTheme.typography.labelMedium,
                color = Blanco,
                modifier = Modifier.semantics {
                    contentDescription = "$aciertos aciertos y $errores errores"
                }
            )
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BlancoTranslucido)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Amarillo)
            )
        }
    }
}

/** Etiqueta corta en Staatliches: "JUEGO DE PREGUNTAS", "ES UN MITO · FALSO". */
@Composable
fun Etiqueta(
    texto: String,
    modifier: Modifier = Modifier,
    fondo: Color = BlancoTranslucido,
    colorTexto: Color = Blanco
) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleSmall,
        color = colorTexto,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(fondo)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

/**
 * Tarjeta del menú de juegos. Toda la tarjeta es tocable.
 * Si [onClick] es null, el juego aparece como "Próximamente".
 */
@Composable
fun TarjetaJuego(
    titulo: String,
    descripcion: String,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val disponible = onClick != null
    val contenido: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .heightIn(min = 120.dp)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
                if (!disponible) {
                    Spacer(Modifier.width(8.dp))
                    Etiqueta("PRÓXIMAMENTE", fondo = AzulNoche, colorTexto = Blanco)
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(text = descripcion, style = MaterialTheme.typography.bodyMedium)
        }
    }
    val forma = RoundedCornerShape(24.dp)
    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = forma,
            color = Blanco,
            contentColor = AzulNoche,
            shadowElevation = 6.dp,
            content = contenido
        )
    } else {
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .alpha(0.7f)
                .semantics { contentDescription = "$titulo, próximamente" },
            shape = forma,
            color = Blanco,
            contentColor = AzulNoche,
            content = contenido
        )
    }
}
