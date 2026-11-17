package com.example.tpjuego.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import com.example.tpjuego.ui.theme.Amarillo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.EstadoNube
import com.example.tpjuego.data.PartidaRemota
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.ResumenJuego
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Resumen del usuario: dos pestañas, sus juegos (en el celular) y sus partidas en la nube. */
@Composable
fun ResumenScreen(
    usuario: String,
    resumenes: List<ResumenJuego>,
    nube: EstadoNube = EstadoNube.NoConfigurada,
    onVolver: () -> Unit
) {
    val totalPartidas = resumenes.sumOf { it.partidas }
    var verNube by rememberSaveable { mutableStateOf(false) }
    val scrollJuegos = rememberScrollState()
    val scrollNube = rememberScrollState()
    PantallaDegradado {
        Spacer(Modifier.height(16.dp))
        Text("MI RESUMEN", style = MaterialTheme.typography.headlineLarge, color = Blanco)
        Text(
            "${usuario.uppercase()} · $totalPartidas ${if (totalPartidas == 1) "PARTIDA" else "PARTIDAS"}",
            style = MaterialTheme.typography.titleSmall,
            color = Blanco
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Pestania("MIS JUEGOS", activa = !verNube, modifier = Modifier.weight(1f)) { verNube = false }
            Spacer(Modifier.width(8.dp))
            Pestania("EN LA NUBE", activa = verNube, modifier = Modifier.weight(1f)) { verNube = true }
        }
        Spacer(Modifier.height(12.dp))
        // Cada pestaña tiene su propio scroll y ocupa todo el alto disponible.
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(if (verNube) scrollNube else scrollJuegos)
        ) {
            if (verNube) {
                TarjetaNube(nube)
            } else {
                resumenes.forEach {
                    TarjetaResumen(it)
                    Spacer(Modifier.height(12.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        BotonSecundario("Volver al menú", onClick = onVolver)
    }
}

@Composable
private fun Pestania(texto: String, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (activa) Amarillo else Blanco.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, style = MaterialTheme.typography.titleSmall, color = if (activa) AzulNoche else Blanco)
    }
}

@Composable
private fun TarjetaResumen(r: ResumenJuego) {
    Tarjeta {
        Text(r.juego.titulo.uppercase(), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        if (r.partidas == 0) {
            Text("Todavía no jugaste.", style = MaterialTheme.typography.bodyLarge)
            return@Tarjeta
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Dato("${r.partidas}", "jugadas", Modifier.weight(1f))
            Dato(textoMejor(r), etiquetaMejor(r.juego), Modifier.weight(1f))
            Dato(fecha(r.ultimaVezMillis), "última vez", Modifier.weight(1f))
        }
    }
}

/** Últimas partidas guardadas en la base de datos externa (Firestore). */
@Composable
private fun TarjetaNube(nube: EstadoNube) {
    Tarjeta {
        Text("ÚLTIMAS PARTIDAS", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        when (nube) {
            EstadoNube.NoConfigurada -> Text(
                "Base de datos sin configurar: las partidas se guardan solo en este celular.",
                style = MaterialTheme.typography.bodyMedium
            )
            EstadoNube.Cargando -> Text("Cargando…", style = MaterialTheme.typography.bodyLarge)
            is EstadoNube.Error -> Text(nube.mensaje, style = MaterialTheme.typography.bodyLarge)
            is EstadoNube.Lista ->
                if (nube.partidas.isEmpty()) {
                    Text("Todavía no hay partidas guardadas.", style = MaterialTheme.typography.bodyLarge)
                } else {
                    nube.partidas.forEach { FilaNube(it) }
                }
        }
    }
}

@Composable
private fun FilaNube(p: PartidaRemota) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(p.juego.titulo, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Text(
            "${textoResultado(p.juego, p.resultado)} · ${fecha(p.fechaMillis)}",
            style = MaterialTheme.typography.bodyMedium,
            color = AzulNoche
        )
    }
}

private fun textoResultado(juego: Juego, resultado: Int): String = when (juego) {
    Juego.MITO_O_REALIDAD -> "$resultado/${AfirmacionesRepositorio.PREGUNTAS_POR_PARTIDA}"
    Juego.MEMOTEST -> "$resultado intentos"
    Juego.LA_PREVIA -> "$resultado decisiones"
}

@Composable
private fun Dato(valor: String, etiqueta: String, modifier: Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, color = AzulNoche, textAlign = TextAlign.Center)
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    }
}

private fun textoMejor(r: ResumenJuego): String = when (r.juego) {
    Juego.MITO_O_REALIDAD -> "${r.mejor}/${AfirmacionesRepositorio.PREGUNTAS_POR_PARTIDA}"
    else -> "${r.mejor}"
}

private fun etiquetaMejor(juego: Juego): String = when (juego) {
    Juego.MITO_O_REALIDAD -> "mejor puntaje"
    Juego.MEMOTEST -> "menos intentos"
    Juego.LA_PREVIA -> "decisiones"
}

private val formatoFecha = SimpleDateFormat("dd/MM", Locale("es", "AR"))
private fun fecha(millis: Long?): String = millis?.let { formatoFecha.format(Date(it)) } ?: "-"

@Preview(showSystemUi = true)
@Composable
private fun ResumenPreview() {
    TPJuegoTheme {
        ResumenScreen(
            usuario = "vale",
            resumenes = listOf(
                ResumenJuego(Juego.MITO_O_REALIDAD, partidas = 4, mejor = 5, ultimo = 3, ultimaVezMillis = 0L),
                ResumenJuego(Juego.MEMOTEST, partidas = 2, mejor = 9, ultimo = 11, ultimaVezMillis = 0L),
                ResumenJuego(Juego.LA_PREVIA)
            ),
            onVolver = {}
        )
    }
}
