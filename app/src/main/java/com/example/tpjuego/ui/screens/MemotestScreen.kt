package com.example.tpjuego.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpjuego.R
import com.example.tpjuego.data.Tema
import com.example.tpjuego.game.Carta
import com.example.tpjuego.game.EventoMemotest
import com.example.tpjuego.game.MemotestViewModel
import com.example.tpjuego.game.Movimiento
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.multimedia.EscucharMovimiento
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco

// Memotest: juego de Enzo (versión final, rama memotest-final).
// El diseño es el suyo; el estado de la partida pasó a MemotestViewModel (game/Memotest.kt),
// así no se pierde al rotar el celular.

// ---------- COLORES ----------
private val FondoMemotest = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0B8A8F),   // turquesa (arriba)
        Color(0xFF1B2A4A)    // azul oscuro (abajo)
    )
)
private val AzulOscuro = Color(0xFF1B2A4A)

// ---------- VIBRACIÓN ----------
private fun vibrar(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(150)
    }
}

// ---------- SONIDO ----------
private fun reproducirEfecto(context: Context, recurso: Int) {
    val player = MediaPlayer.create(context, recurso) ?: return
    player.setOnCompletionListener { it.release() }
    player.start()
}

// ---------- [COMPOSABLE] PANTALLA PRINCIPAL ----------
@Composable
fun MemotestScreen(
    onVolver: () -> Unit = {},
    viewModel: MemotestViewModel = viewModel()
) {
    val context = LocalContext.current
    val juego by viewModel.juego.collectAsState()
    val mejorMarca by viewModel.mejorMarca.collectAsState()
    val nuevoRecord by viewModel.nuevoRecord.collectAsState()
    val temas = juego.temas

    // ---------- MÚSICA DE FONDO ----------
    val musica = remember {
        MediaPlayer.create(context, R.raw.music5)?.apply {
            isLooping = true
            setVolume(0.4f, 0.4f)
        }
    }
    val finalActual by rememberUpdatedState(juego.mostrarFinal)

    // Suena con la pantalla visible, se pausa en onPause y se libera al salir.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (!finalActual) musica?.start()
                Lifecycle.Event.ON_PAUSE -> musica?.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            musica?.release()
        }
    }

    // En la pantalla final se pausa la música de fondo; si se reinicia el juego, vuelve.
    LaunchedEffect(juego.mostrarFinal) {
        if (juego.mostrarFinal) musica?.pause() else musica?.start()
    }

    // Sonido y vibración: llegan como eventos del ViewModel, así no se repiten al rotar.
    LaunchedEffect(viewModel) {
        viewModel.eventos.collect { evento ->
            when (evento) {
                EventoMemotest.ParEncontrado -> {
                    vibrar(context)
                    reproducirEfecto(context, R.raw.acierto1)
                }
                EventoMemotest.JuegoCompletado -> reproducirEfecto(context, R.raw.juegocompletado1)
            }
        }
    }

    // Sensor (acelerómetro): en la pantalla final, sacudir el celular arranca otra partida.
    EscucharMovimiento(activo = juego.mostrarFinal) { movimiento ->
        if (movimiento == Movimiento.SACUDIR) viewModel.reiniciar()
    }

    if (juego.mostrarFinal) {
        // ---------- PANTALLA FINAL (estética de ¿Mito o Realidad?) ----------
        FinalMemotestContent(
            intentos = juego.intentos,
            notas = juego.notas,
            temas = temas,
            mejorMarca = mejorMarca,
            nuevoRecord = nuevoRecord,
            onJugarDeNuevo = viewModel::reiniciar,
            onTerminar = onVolver
        )
        return
    }

    // ---------- JUEGO ----------
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(brush = FondoMemotest)
            .systemBarsPadding()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Memotest", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("Intentos: ${juego.intentos}", fontSize = 19.sp, color = Color.White)
            }
        }

        itemsIndexed(juego.cartas) { index, carta ->
            CartaItem(carta) { viewModel.tocar(index) }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                Text(
                    "Notas encontradas",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    if (juego.notas.isEmpty()) "Todavía no encontraste ninguna pareja."
                    else "Tocá una nota para volver a leerla.",
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
        }

        items(juego.notas, span = { GridItemSpan(maxLineSpan) }) { id ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                    contentColor = AzulOscuro
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.abrirNota(id) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(temas[id].nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(Modifier.height(2.dp))
                    Text(temas[id].nota, fontSize = 16.sp)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(onClick = onVolver) { Text("Volver", fontSize = 17.sp) }
            }
        }
    }

    // Alerta en el centro con la nota
    juego.notaAbierta?.let { id ->
        Dialog(onDismissRequest = viewModel::cerrarNota) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cerrarNota() }
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        temas[id].nombre,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = AzulOscuro
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        temas[id].nota,
                        fontSize = 18.sp,
                        lineHeight = 25.sp,
                        textAlign = TextAlign.Center,
                        color = AzulOscuro
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Tocá la pantalla para cerrar",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF616161)
                    )
                }
            }
        }
    }
}

// ---------- [COMPOSABLE] PANTALLA FINAL ----------
// Misma estructura que FinalContent de ¿Mito o Realidad?: degradé, tarjetas blancas y dos botones.
@Composable
private fun FinalMemotestContent(
    intentos: Int,
    notas: List<Int>,
    temas: List<Tema>,
    mejorMarca: Int?,
    nuevoRecord: Boolean,
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

            // Tarjeta con los intentos y el récord
            Tarjeta(alineacion = Alignment.CenterHorizontally) {
                Text(text = "$intentos", style = MaterialTheme.typography.displayLarge)
                Text(
                    text = if (intentos == 1) "intento" else "intentos",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when {
                        nuevoRecord -> "¡NUEVO RÉCORD!"
                        mejorMarca != null -> "Tu récord: $mejorMarca"
                        else -> ""
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = if (nuevoRecord) AzulNoche else AzulNoche.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Encontraste las ${temas.size} parejas. Cada dato que leíste suma.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(16.dp))

            // Resumen de las notas descubiertas, en el orden en que las encontraste
            Tarjeta {
                Text(text = "LO QUE DESCUBRISTE", style = MaterialTheme.typography.titleSmall)
                notas.forEachIndexed { i, id ->
                    Spacer(Modifier.height(12.dp))
                    if (i > 0) {
                        HorizontalDivider(color = AzulNoche.copy(alpha = 0.12f))
                        Spacer(Modifier.height(12.dp))
                    }
                    Text(text = temas[id].nombre, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Text(text = temas[id].nota, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                text = "¿Querés hablar con alguien? Línea 141 de SEDRONAR: gratuita, anónima " +
                        "y las 24 horas.",
                style = MaterialTheme.typography.bodyMedium,
                color = Blanco,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "SACUDÍ EL CELU PARA JUGAR OTRA VEZ",
                style = MaterialTheme.typography.labelMedium,
                color = Amarillo,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Jugar de nuevo", onClick = onJugarDeNuevo)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Terminar y volver al menú", onClick = onTerminar)
    }
}

// ---------- [COMPOSABLE] UNA CARTA (con animación de giro) ----------
@Composable
private fun CartaItem(carta: Carta, onClick: () -> Unit) {
    val visible = carta.volteada || carta.encontrada

    val rotacion by animateFloatAsState(
        targetValue = if (visible) 180f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "giroCarta"
    )
    val mostrarFrente = rotacion > 90f

    val colorFondo = when {
        !mostrarFrente -> Color(0xFF3F51B5)   // azul: tapada
        carta.encontrada -> Color(0xFF2E7D32) // verde: acertada
        else -> Color(0xFFF57C00)             // naranja: dada vuelta
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .graphicsLayer {
                rotationY = rotacion
                cameraDistance = 12f * density
            }
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo)
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (mostrarFrente) carta.texto else "?",
            color = Color.White,
            fontSize = if (mostrarFrente) 16.sp else 30.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { rotationY = if (mostrarFrente) 180f else 0f }
        )
    }
}
