package com.example.tpjuego.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpjuego.data.LaPreviaHistoria
import com.example.tpjuego.game.Historia
import com.example.tpjuego.game.LaPreviaViewModel
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.Etiqueta
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.BlancoTranslucido
import com.example.tpjuego.ui.theme.TPJuegoTheme
import com.example.tpjuego.ui.theme.Turquesa

// La Previa: historia en escenas donde cada decisión lleva a su camino.
// La lógica vive en Historia/ViewModel; acá solo se dibuja según el estado.
// No hay respuestas correctas ni incorrectas: por eso las opciones son blancas
// (no verde/rojo como en ¿Mito o Realidad?) y el fondo no cambia según lo elegido.
@Composable
fun LaPreviaScreen(
    onTerminar: () -> Unit,
    viewModel: LaPreviaViewModel = viewModel()
) {
    val historia by viewModel.historia.collectAsState()
    val empezo by viewModel.empezo.collectAsState()

    if (!empezo) {
        PortadaLaPrevia(onComenzar = viewModel::comenzar, onVolver = onTerminar)
        return
    }

    Crossfade(targetState = historia, label = "fase de la historia") { h ->
        when {
            h.terminada -> FinalLaPrevia(
                historia = h,
                onJugarDeNuevo = viewModel::jugarDeNuevo,
                onTerminar = onTerminar
            )
            h.elegida == null -> EscenaContent(h, onElegir = viewModel::elegir)
            else -> ConsecuenciaContent(h, onSeguir = viewModel::seguir)
        }
    }
}

@Composable
private fun PortadaLaPrevia(onComenzar: () -> Unit, onVolver: () -> Unit) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))
            Etiqueta("MODO HISTORIA", fondo = Amarillo, colorTexto = AzulNoche)
            Spacer(Modifier.height(16.dp))
            Text("LA PREVIA", style = MaterialTheme.typography.displayLarge, color = Blanco)
            Spacer(Modifier.height(8.dp))
            Text(
                "Una noche. Tus decisiones.",
                style = MaterialTheme.typography.titleMedium,
                color = Blanco
            )
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                Text(
                    "Es sábado y hay previa. En ${LaPreviaHistoria.TOTAL_ESCENAS} escenas vas a " +
                        "elegir cómo reaccionar.",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "No hay respuestas buenas o malas: cada camino te muestra qué puede pasar.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Comenzar", onClick = onComenzar)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Volver al menú", onClick = onVolver)
    }
}

/** "ESCENA X DE 6" con la barra amarilla, sin contador de aciertos (acá no hay puntaje). */
@Composable
private fun BarraEscena(numero: Int, total: Int) {
    val progreso by animateFloatAsState(
        targetValue = numero.toFloat() / total,
        label = "progreso de la historia"
    )
    Column(Modifier.fillMaxWidth()) {
        Text(
            "ESCENA $numero DE $total",
            style = MaterialTheme.typography.labelMedium,
            color = Blanco
        )
        Spacer(Modifier.height(8.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(BlancoTranslucido)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progreso)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Amarillo)
            )
        }
    }
}

@Composable
private fun EscenaContent(historia: Historia, onElegir: (Int) -> Unit) {
    val escena = historia.actual
    PantallaDegradado {
        BarraEscena(escena.numero, historia.totalEscenas)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text(escena.titulo, style = MaterialTheme.typography.headlineLarge, color = Blanco)
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text(escena.situacion, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(16.dp))
        }
        Text("¿QUÉ HACÉS?", style = MaterialTheme.typography.titleSmall, color = Amarillo)
        Spacer(Modifier.height(10.dp))
        // Las opciones van abajo, al alcance del pulgar.
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            escena.opciones.forEachIndexed { i, opcion ->
                BotonEleccion(letra = ('A' + i).toString(), texto = opcion.texto, onClick = { onElegir(i) })
            }
        }
    }
}

/** Botón de opción: blanco y neutro, con la letra en un círculo. Puede ocupar dos líneas. */
@Composable
private fun BotonEleccion(letra: String, texto: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 58.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blanco,
        contentColor = AzulNoche,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Turquesa),
                contentAlignment = Alignment.Center
            ) {
                Text(letra, style = MaterialTheme.typography.titleSmall, color = Blanco)
            }
            Spacer(Modifier.width(12.dp))
            Text(texto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ConsecuenciaContent(historia: Historia, onSeguir: () -> Unit) {
    val opcion = historia.elegida ?: return
    PantallaDegradado {
        BarraEscena(historia.actual.numero, historia.totalEscenas)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Etiqueta("ELEGISTE")
            Spacer(Modifier.height(8.dp))
            Text(opcion.texto, style = MaterialTheme.typography.bodyLarge, color = Blanco)
            Spacer(Modifier.height(16.dp))
            Text(opcion.titulo, style = MaterialTheme.typography.headlineLarge, color = Blanco)
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text(opcion.consecuencia, style = MaterialTheme.typography.bodyLarge)
            }
            opcion.recurso?.let { recurso ->
                Spacer(Modifier.height(16.dp))
                Tarjeta {
                    Etiqueta("RECURSO", fondo = Amarillo, colorTexto = AzulNoche)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        recurso.texto,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Fuente: ${recurso.fuente}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        val ultima = opcion.siguiente == null
        BotonPrimario(if (ultima) "Ver cómo terminó la noche" else "Seguir", onClick = onSeguir)
    }
}

@Composable
private fun FinalLaPrevia(historia: Historia, onJugarDeNuevo: () -> Unit, onTerminar: () -> Unit) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text("FIN DE LA NOCHE", style = MaterialTheme.typography.headlineLarge, color = Blanco)
            Spacer(Modifier.height(8.dp))
            Text(
                "Llegaste a casa. Así fue tu camino:",
                style = MaterialTheme.typography.titleMedium,
                color = Blanco
            )
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text("TU RECORRIDO", style = MaterialTheme.typography.titleSmall, color = Turquesa)
                historia.recorrido.forEachIndexed { i, paso ->
                    Spacer(Modifier.height(8.dp))
                    Text("${i + 1}. ${paso.opcion.resumen}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Etiqueta("LO QUE TE LLEVÁS", fondo = Amarillo, colorTexto = AzulNoche)
                historia.recursos.forEach { recurso ->
                    Spacer(Modifier.height(10.dp))
                    Text("• ${recurso.texto}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                "Podés volver a jugar y elegir otro camino.",
                style = MaterialTheme.typography.bodyMedium,
                color = Blanco,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Jugar otro camino", onClick = onJugarDeNuevo)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Terminar y volver al menú", onClick = onTerminar)
    }
}

// ---------- Previews ----------

private fun historiaEnConsecuencia() = Historia().elegir(0).seguir().elegir(1)

private fun historiaTerminada(): Historia {
    var h = Historia()
    while (!h.terminada) h = h.elegir(0).seguir()
    return h
}

@Preview(showSystemUi = true)
@Composable
private fun PortadaLaPreviaPreview() {
    TPJuegoTheme { PortadaLaPrevia(onComenzar = {}, onVolver = {}) }
}

@Preview(showSystemUi = true)
@Composable
private fun EscenaPreview() {
    TPJuegoTheme { EscenaContent(Historia().elegir(0).seguir(), onElegir = {}) }
}

@Preview(showSystemUi = true)
@Composable
private fun ConsecuenciaPreview() {
    TPJuegoTheme { ConsecuenciaContent(historiaEnConsecuencia(), onSeguir = {}) }
}

@Preview(showSystemUi = true)
@Composable
private fun FinalLaPreviaPreview() {
    TPJuegoTheme { FinalLaPrevia(historiaTerminada(), onJugarDeNuevo = {}, onTerminar = {}) }
}
