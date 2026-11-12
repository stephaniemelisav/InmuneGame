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
import com.example.tpjuego.R
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

// ---------- MODELOS ----------
private data class Tema(val nombre: String, val nota: String)

data class Carta(
    val parejaId: Int,
    val texto: String,
    val volteada: Boolean = false,
    val encontrada: Boolean = false
)

// ---------- COLORES ----------
private val FondoMemotest = Brush.verticalGradient(
    colors = listOf(
        Color(0xFF0B8A8F),   // turquesa (arriba)
        Color(0xFF1B2A4A)    // azul oscuro (abajo)
    )
)
private val AzulOscuro = Color(0xFF1B2A4A)

// ---------- DATOS ----------
private val temas = listOf(
    Tema("Alcohol", "El alcohol deprime el sistema nervioso central y altera los reflejos y el juicio."),
    Tema("Tabaco", "La nicotina genera alta dependencia."),
    Tema("Marihuana", "La marihuana afecta la memoria y la atención."),
    Tema("Energizante + alcohol", "Mezclar energizante con alcohol disimula la intoxicación y es muy riesgoso."),
    Tema("Cocaína", "La cocaína aumenta el riesgo de problemas cardíacos."),
    Tema("Pedir ayuda", "Hablar con una persona de confianza es el primer paso para cuidarte.")
)

private fun generarCartas(): List<Carta> =
    temas.flatMapIndexed { i, tema ->
        listOf(Carta(i, tema.nombre), Carta(i, tema.nombre))
    }.shuffled()

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
fun MemotestScreen(onVolver: () -> Unit = {}) {
    val context = LocalContext.current

    var cartas by remember { mutableStateOf(generarCartas()) }
    var seleccion by remember { mutableStateOf(listOf<Int>()) }
    var intentos by remember { mutableIntStateOf(0) }
    var notas by remember { mutableStateOf(listOf<Int>()) }
    var notaAbierta by remember { mutableStateOf<Int?>(null) }
    var finalMostrado by remember { mutableStateOf(false) }
    var mostrarFinal by remember { mutableStateOf(false) }

    // ---------- MÚSICA DE FONDO ----------
    val musica = remember {
        MediaPlayer.create(context, R.raw.music5)?.apply {
            isLooping = true
            setVolume(0.4f, 0.4f)
        }
    }
    val finalActual by rememberUpdatedState(mostrarFinal)

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

    // Al aparecer la pantalla final: se pausa la música de fondo y suena "juego completado".
    // Si se reinicia el juego, la música de fondo vuelve.
    LaunchedEffect(mostrarFinal) {
        if (mostrarFinal) {
            musica?.pause()
            reproducirEfecto(context, R.raw.juegocompletado1)
        } else {
            musica?.start()
        }
    }

    // Compara las cartas cuando hay 2 dadas vuelta
    LaunchedEffect(seleccion) {
        if (seleccion.size == 2) {
            val (a, b) = seleccion
            intentos++

            if (cartas[a].parejaId == cartas[b].parejaId) {
                cartas = cartas.toMutableList().also {
                    it[a] = it[a].copy(encontrada = true)
                    it[b] = it[b].copy(encontrada = true)
                }
                val id = cartas[a].parejaId
                notas = notas + id
                notaAbierta = id
                vibrar(context)
                reproducirEfecto(context, R.raw.acierto1)
            } else {
                delay(1.seconds)
                cartas = cartas.toMutableList().also {
                    it[a] = it[a].copy(volteada = false)
                    it[b] = it[b].copy(volteada = false)
                }
            }
            seleccion = emptyList()
        }
    }

    // Cierra la alerta y, si era la última pareja, pasa a la pantalla final (una sola vez)
    fun cerrarNota() {
        notaAbierta = null
        if (cartas.all { it.encontrada } && !finalMostrado) {
            finalMostrado = true
            mostrarFinal = true
        }
    }

    fun reiniciar() {
        cartas = generarCartas()
        seleccion = emptyList()
        intentos = 0
        notas = emptyList()
        notaAbierta = null
        finalMostrado = false
        mostrarFinal = false
    }

    if (mostrarFinal) {
        // ---------- PANTALLA FINAL (estética de ¿Mito o Realidad?) ----------
        FinalMemotestContent(
            intentos = intentos,
            notas = notas,
            onJugarDeNuevo = { reiniciar() },
            onTerminar = onVolver
        )
    } else {
        // ---------- JUEGO ----------
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .background(brush = FondoMemotest)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Memotest", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("Intentos: $intentos", fontSize = 18.sp, color = Color.White)
                }
            }

            itemsIndexed(cartas) { index, carta ->
                CartaItem(carta) {
                    if (notaAbierta == null && seleccion.size < 2 &&
                        !carta.volteada && !carta.encontrada
                    ) {
                        cartas = cartas.toMutableList().also {
                            it[index] = it[index].copy(volteada = true)
                        }
                        seleccion = seleccion + index
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text(
                        "Notas encontradas",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.height(4.dp))
                    if (notas.isEmpty()) {
                        Text("Todavía no encontraste ninguna pareja.", fontSize = 14.sp, color = Color.White)
                    } else {
                        Text("Tocá una nota para volver a leerla.", fontSize = 14.sp, color = Color.White)
                    }
                }
            }

            items(notas, span = { GridItemSpan(maxLineSpan) }) { id ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = AzulOscuro
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { if (notaAbierta == null) notaAbierta = id }
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(temas[id].nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(Modifier.height(2.dp))
                        Text(temas[id].nota, fontSize = 14.sp)
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Button(onClick = onVolver) { Text("Volver") }
                }
            }
        }

        // Alerta en el centro con la nota
        notaAbierta?.let { id ->
            Dialog(onDismissRequest = { cerrarNota() }) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { cerrarNota() }
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            temas[id].nombre,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = AzulOscuro
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            temas[id].nota,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = AzulOscuro
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "Tocá la pantalla para dejar de leer la nota",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF616161)
                        )
                    }
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

            // Tarjeta con los intentos
            Tarjeta(alineacion = Alignment.CenterHorizontally) {
                Text(
                    text = "$intentos",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = if (intentos == 1) "intento" else "intentos",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(16.dp))
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
                    Text(
                        text = temas[id].nombre,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = temas[id].nota,
                        style = MaterialTheme.typography.bodyMedium
                    )
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer { rotationY = if (mostrarFrente) 180f else 0f }
        )
    }
}