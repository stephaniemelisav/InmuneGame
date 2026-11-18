package com.example.tpjuego.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tpjuego.game.EventoReloj
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.ResultadoGuardado
import com.example.tpjuego.game.MitoORealidadViewModel
import com.example.tpjuego.game.Movimiento
import com.example.tpjuego.ui.multimedia.EscucharMovimiento
import com.example.tpjuego.ui.multimedia.rememberEfectos
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.game.Partida
import com.example.tpjuego.game.RespuestaDada
import com.example.tpjuego.ui.components.BarraProgreso
import com.example.tpjuego.ui.components.BotonOpcion
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.Etiqueta
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.DegradeAcierto
import com.example.tpjuego.ui.theme.DegradeError
import com.example.tpjuego.ui.theme.NaranjaEstado
import com.example.tpjuego.ui.theme.RojoFalso
import com.example.tpjuego.ui.theme.Staatliches
import com.example.tpjuego.ui.theme.TPJuegoTheme
import com.example.tpjuego.ui.theme.Turquesa
import com.example.tpjuego.ui.theme.VerdeEstado
import com.example.tpjuego.ui.theme.VerdeVerdadero
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.tpjuego.R  // <- NUEVO
import com.example.tpjuego.ui.components.MusicaDeFondo  // <- NUEVO

// Partida de ¿Mito o Realidad?: afirmación -> resultado -> ... -> pantalla final.
// La lógica vive en Partida/ViewModel; acá solo se dibuja según el estado.
@Composable
fun GameScreen(
    onTerminar: () -> Unit,
    viewModel: MitoORealidadViewModel = viewModel()
) {
    MusicaDeFondo(R.raw.music3)   // <- NUEVO

    val partida by viewModel.partida.collectAsState()
    val anteriores by viewModel.partidasAnteriores.collectAsState()
    val segundos by viewModel.segundosRestantes.collectAsState()
    val efectos = rememberEfectos()

    // Reloj: corre solo con la pantalla en primer plano (se frena en onPause).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> viewModel.arrancarReloj()
                Lifecycle.Event.ON_PAUSE -> viewModel.pausarReloj()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.pausarReloj()
        }
    }

    // Tic cada segundo; si se acaba el tiempo, cuenta como error: suena y vibra.
    LaunchedEffect(viewModel) {
        viewModel.eventosReloj.collect { evento ->
            when (evento) {
                is EventoReloj.Tic -> efectos.sonarTic(urgente = evento.segundosRestantes <= 3)
                EventoReloj.SeAcaboElTiempo -> {
                    efectos.sonarError()
                    efectos.vibrar()
                }
            }
        }
    }

    // Responder con sonido; si se equivoca, además vibra.
    val responder: (Boolean) -> Unit = { eligioRealidad ->
        if (!viewModel.partida.value.respondida) {
            viewModel.responder(eligioRealidad)
            if (viewModel.partida.value.acerto == true) {
                efectos.sonarAcierto()
            } else {
                efectos.sonarError()
                efectos.vibrar()
            }
        }
    }

    // Sensor: inclinar el celular a la derecha = Verdadero, a la izquierda = Falso.
    // Solo escucha mientras hay una pregunta sin responder.
    EscucharMovimiento(activo = !partida.terminada && !partida.respondida) { movimiento ->
        when (movimiento) {
            Movimiento.INCLINAR_DERECHA -> responder(true)
            Movimiento.INCLINAR_IZQUIERDA -> responder(false)
            Movimiento.SACUDIR -> Unit
        }
    }

    Crossfade(targetState = partida, label = "fase de la partida") { p ->
        when {
            p.terminada -> FinalContent(
                partida = p,
                partidasAnteriores = anteriores,
                onJugarDeNuevo = viewModel::jugarDeNuevo,
                onTerminar = onTerminar
            )
            !p.respondida -> PreguntaContent(p, segundos = segundos, onResponder = responder)
            else -> ResultadoContent(p, onSiguiente = {
                viewModel.siguiente()
                if (viewModel.partida.value.terminada) efectos.sonarFin()
            })
        }
    }
}

@Composable
private fun BarraDePartida(partida: Partida) {
    BarraProgreso(
        actual = partida.numeroPregunta,
        total = partida.total,
        aciertos = partida.aciertos,
        errores = partida.errores
    )
}

@Composable
private fun PreguntaContent(partida: Partida, segundos: Int, onResponder: (Boolean) -> Unit) {
    PantallaDegradado {
        BarraDePartida(partida)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "¿VERDADERO O FALSO?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Blanco,
                    modifier = Modifier.weight(1f)
                )
                Reloj(segundos)
            }
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text(
                    text = "“",
                    fontFamily = Staatliches,
                    fontSize = 64.sp,
                    lineHeight = 48.sp,
                    color = Turquesa
                )
                Text(
                    text = partida.actual.texto,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        // Ayuda del sensor: cada lado del celular muestra a qué respuesta corresponde.
        Text(
            text = "O INCLINÁ EL CELU",
            style = MaterialTheme.typography.labelMedium,
            color = Amarillo,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("← FALSO", style = MaterialTheme.typography.labelMedium, color = Blanco, modifier = Modifier.weight(1f))
            Text("VERDADERO →", style = MaterialTheme.typography.labelMedium, color = Blanco)
        }
        Spacer(Modifier.height(10.dp))
        // Verde y rojo nunca van solos: cada botón lleva ícono y texto.
        BotonOpcion("✓  Verdadero", fondo = VerdeVerdadero, onClick = { onResponder(true) })
        Spacer(Modifier.height(12.dp))
        BotonOpcion("✕  Falso", fondo = RojoFalso, onClick = { onResponder(false) })
    }
}

@Composable
private fun ResultadoContent(partida: Partida, onSiguiente: () -> Unit) {
    val acerto = partida.acerto == true
    val afirmacion = partida.actual

    PantallaDegradado(colores = if (acerto) DegradeAcierto else DegradeError) {
        BarraDePartida(partida)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            IconoResultado(acerto, tamanio = 96)
            Spacer(Modifier.height(16.dp))
            Text(
                // El error no se vive como castigo: el foco está en la explicación.
                text = when {
                    acerto -> "¡RESPUESTA CORRECTA!"
                    partida.sinTiempo -> "¡SE ACABÓ EL TIEMPO!"
                    else -> "ESTA VEZ NO"
                },
                style = MaterialTheme.typography.headlineLarge,
                color = Blanco,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Etiqueta(if (afirmacion.esRealidad) "ES REALIDAD · VERDADERO" else "ES UN MITO · FALSO")
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                Text(
                    text = "“${afirmacion.texto}”",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(12.dp))
                Text(text = afirmacion.explicacion, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Fuente: ${afirmacion.fuente}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AzulNoche.copy(alpha = 0.75f)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario(
            texto = if (partida.esUltima) "Ver mi resultado" else "Siguiente",
            onClick = onSiguiente
        )
    }
}

/** Cuenta regresiva: número grande en un círculo. Los últimos 3 segundos se pone naranja. */
@Composable
private fun Reloj(segundos: Int) {
    val urgente = segundos <= 3
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(if (urgente) NaranjaEstado else Blanco)
            .semantics { contentDescription = "Quedan $segundos segundos" },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$segundos",
            fontFamily = Staatliches,
            fontSize = 36.sp,
            color = if (urgente) Blanco else AzulNoche
        )
    }
}

/** Círculo con ✓ o ✕: refuerza el resultado sin depender del color. */
@Composable
private fun IconoResultado(acerto: Boolean, tamanio: Int, relleno: Boolean = false) {
    val colorEstado = if (acerto) VerdeEstado else NaranjaEstado
    Box(
        modifier = Modifier
            .size(tamanio.dp)
            .clip(CircleShape)
            .background(if (relleno) colorEstado else Blanco)
            .semantics {
                contentDescription = if (acerto) "Respuesta correcta" else "Respuesta incorrecta"
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (acerto) "✓" else "✕",
            fontSize = (tamanio / 2).sp,
            fontWeight = FontWeight.Bold,
            color = if (relleno) Blanco else colorEstado
        )
    }
}

@Composable
private fun FinalContent(
    partida: Partida,
    partidasAnteriores: List<ResultadoGuardado>,
    onJugarDeNuevo: () -> Unit,
    onTerminar: () -> Unit
) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "¡JUEGO TERMINADO!",
                style = MaterialTheme.typography.headlineLarge,
                color = Blanco,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))

            // Puntaje y contador. Sin "ganaste" ni "perdiste": es lo que sabe hoy.
            Tarjeta(alineacion = Alignment.CenterHorizontally) {
                Text(
                    text = "${partida.aciertos} / ${partida.total}",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "respuestas correctas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Contador(partida.aciertos, "aciertos", acerto = true)
                    Spacer(Modifier.width(24.dp))
                    Contador(partida.errores, if (partida.errores == 1) "error" else "errores", acerto = false)
                }
            }
            Spacer(Modifier.height(16.dp))

            // Resumen pregunta por pregunta.
            Tarjeta {
                Text(text = "TUS RESPUESTAS", style = MaterialTheme.typography.titleSmall)
                partida.respuestas.forEachIndexed { i, respuesta ->
                    Spacer(Modifier.height(12.dp))
                    if (i > 0) {
                        HorizontalDivider(color = AzulNoche.copy(alpha = 0.12f))
                        Spacer(Modifier.height(12.dp))
                    }
                    FilaResumen(respuesta)
                }
            }

            // Historial de partidas en este celular.
            if (partidasAnteriores.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Tarjeta {
                    Text(text = "TUS ÚLTIMAS PARTIDAS", style = MaterialTheme.typography.titleSmall)
                    partidasAnteriores.take(5).forEachIndexed { i, resultado ->
                        Spacer(Modifier.height(10.dp))
                        FilaHistorial(resultado, esLaActual = i == 0)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "¿Querés hablar? Línea 141 · gratis y anónima, 24 h",
                style = MaterialTheme.typography.bodyLarge,
                color = Blanco,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Jugar de nuevo", onClick = onJugarDeNuevo)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Terminar y volver al menú", onClick = onTerminar)
    }
}

@Composable
private fun Contador(cantidad: Int, texto: String, acerto: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconoResultado(acerto = acerto, tamanio = 32, relleno = true)
        Spacer(Modifier.width(8.dp))
        Text(text = "$cantidad $texto", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun FilaResumen(respuesta: RespuestaDada) {
    Row(verticalAlignment = Alignment.Top) {
        IconoResultado(acerto = respuesta.acerto, tamanio = 28, relleno = true)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = respuesta.afirmacion.texto, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                text = when (respuesta.eligioRealidad) {
                    null -> "SIN TIEMPO"
                    true -> "RESPONDISTE VERDADERO"
                    false -> "RESPONDISTE FALSO"
                } + " · " +
                    if (respuesta.afirmacion.esRealidad) "ERA REALIDAD" else "ERA UN MITO",
                style = MaterialTheme.typography.labelMedium,
                color = if (respuesta.acerto) VerdeEstado else NaranjaEstado
            )
        }
    }
}

private val formatoFecha = SimpleDateFormat("dd/MM · HH:mm", Locale("es", "AR"))

@Composable
private fun FilaHistorial(resultado: ResultadoGuardado, esLaActual: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = if (esLaActual) "Esta partida" else formatoFecha.format(Date(resultado.fechaMillis)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (esLaActual) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "✓ ${resultado.aciertos}   ✕ ${resultado.errores}",
            style = MaterialTheme.typography.labelMedium,
            color = AzulNoche
        )
    }
}

// ---------- Previews de cada fase ----------

private val partidaEjemplo = Partida(AfirmacionesRepositorio.todas.take(5))

private fun partidaTerminadaEjemplo(): Partida {
    var p = partidaEjemplo
    listOf(false, false, false, true, true).forEach { p = p.responder(it).siguiente() }
    return p
}

@Preview(showSystemUi = true, name = "Pregunta")
@Composable
private fun PreguntaPreview() {
    TPJuegoTheme { PreguntaContent(partidaEjemplo, segundos = 7, onResponder = {}) }
}

@Preview(showSystemUi = true, name = "Acierto")
@Composable
private fun AciertoPreview() {
    TPJuegoTheme { ResultadoContent(partidaEjemplo.responder(false), onSiguiente = {}) }
}

@Preview(showSystemUi = true, name = "Error")
@Composable
private fun ErrorPreview() {
    TPJuegoTheme { ResultadoContent(partidaEjemplo.responder(true), onSiguiente = {}) }
}

@Preview(showSystemUi = true, name = "Final")
@Composable
private fun FinalPreview() {
    TPJuegoTheme {
        FinalContent(
            partida = partidaTerminadaEjemplo(),
            partidasAnteriores = listOf(
                ResultadoGuardado(System.currentTimeMillis(), 4, 5),
                ResultadoGuardado(System.currentTimeMillis() - 86_400_000, 2, 5)
            ),
            onJugarDeNuevo = {},
            onTerminar = {}
        )
    }
}
