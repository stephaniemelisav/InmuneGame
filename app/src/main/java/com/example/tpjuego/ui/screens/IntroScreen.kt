package com.example.tpjuego.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.VideoView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.DegradeGeneral
import com.example.tpjuego.ui.theme.TPJuegoTheme
import com.example.tpjuego.ui.theme.Turquesa
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/**
 * Introducción multimedia de concientización, al abrir la app.
 *
 * - Si existe el archivo res/raw/intro.mp4, se reproduce ese video.
 * - Si no, se muestra una animación hecha con Compose.
 *
 * En los dos casos se puede saltear, y al terminar pasa al menú de juegos.
 */
@SuppressLint("DiscouragedApi") // getIdentifier: permite que el video sea opcional.
@Composable
fun IntroScreen(onTerminar: () -> Unit) {
    val context = LocalContext.current
    val videoId = remember {
        context.resources.getIdentifier("intro", "raw", context.packageName)
    }

    // Evita navegar dos veces si el video termina justo cuando se toca "Saltar".
    var terminada by remember { mutableStateOf(false) }
    val terminar = {
        if (!terminada) {
            terminada = true
            onTerminar()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulNoche)
    ) {
        if (videoId != 0) {
            VideoIntro(videoId = videoId, onFin = terminar)
        } else {
            AnimacionIntro(onFin = terminar)
        }
        TextButton(
            onClick = terminar,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .systemBarsPadding()
                .padding(16.dp)
        ) {
            Text("Saltar  ›", style = MaterialTheme.typography.labelLarge, color = Blanco)
        }
    }
}

@Composable
private fun VideoIntro(videoId: Int, onFin: () -> Unit) {
    val fin by rememberUpdatedState(onFin)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { ctx ->
                VideoView(ctx).apply {
                    setVideoURI(Uri.parse("android.resource://${ctx.packageName}/$videoId"))
                    setOnCompletionListener { fin() }
                    // Si el video no se puede reproducir, sigue al menú en vez de trabarse.
                    setOnErrorListener { _, _, _ ->
                        fin()
                        true
                    }
                    start()
                }
            },
            onRelease = { it.stopPlayback() }
        )
    }
}

// Mensajes de concientización: sin sermón, en la línea de "dudar antes de creer".
private val escenas = listOf(
    "Todos los días escuchamos cosas sobre las drogas.",
    "Algunas son verdad.\nMuchas, no.",
    "Los mitos también se contagian.",
    "Informarte te vuelve inmune."
)

@Composable
private fun AnimacionIntro(onFin: () -> Unit) {
    val fin by rememberUpdatedState(onFin)
    var escena by remember { mutableIntStateOf(0) }
    val opacidad = remember { Animatable(0f) }
    val desplazamiento = remember { Animatable(24f) }
    val escala = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        for (i in escenas.indices) {
            escena = i
            opacidad.snapTo(0f)
            desplazamiento.snapTo(24f)
            launch { desplazamiento.animateTo(0f, tween(700)) }
            opacidad.animateTo(1f, tween(700))
            delay(1600)
            opacidad.animateTo(0f, tween(450))
        }
        // Escena final: el logo con un rebote.
        escena = escenas.size
        opacidad.snapTo(0f)
        launch { escala.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 120f)) }
        opacidad.animateTo(1f, tween(600))
        delay(2000)
        fin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(DegradeGeneral)),
        contentAlignment = Alignment.Center
    ) {
        Particulas()
        if (escena < escenas.size) {
            Text(
                text = escenas[escena],
                style = MaterialTheme.typography.headlineLarge,
                color = Blanco,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .graphicsLayer {
                        alpha = opacidad.value
                        translationY = desplazamiento.value
                    }
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = opacidad.value
                    scaleX = escala.value
                    scaleY = escala.value
                }
            ) {
                Text(
                    text = "INMUNE GAME",
                    style = MaterialTheme.typography.displayLarge,
                    color = Amarillo
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Dudar antes de creer",
                    style = MaterialTheme.typography.titleMedium,
                    color = Blanco
                )
            }
        }
    }
}

/** Círculos que giran alrededor del centro, como defensas que rodean al mensaje. */
@Composable
private fun Particulas() {
    val transicion = rememberInfiniteTransition(label = "particulas")
    val giro by transicion.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing)),
        label = "giro"
    )
    val pulso by transicion.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "pulso"
    )
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centro = Offset(size.width / 2, size.height / 2)
        val radioOrbita = size.minDimension * 0.42f
        val cantidad = 8
        for (i in 0 until cantidad) {
            val angulo = giro + i * (2 * Math.PI / cantidad).toFloat()
            val punto = Offset(
                x = centro.x + radioOrbita * cos(angulo),
                y = centro.y + radioOrbita * 1.4f * sin(angulo)
            )
            val color = if (i % 2 == 0) Amarillo else Turquesa
            drawCircle(
                color = color.copy(alpha = 0.35f),
                radius = (10 + (i % 3) * 6) * pulso * density,
                center = punto
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun IntroScreenPreview() {
    TPJuegoTheme {
        IntroScreen(onTerminar = {})
    }
}
