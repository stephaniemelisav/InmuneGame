package com.example.tpjuego.ui.multimedia

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale

/**
 * Voz de la intro: lee los mensajes en voz alta con el lector de texto de Android (TextToSpeech).
 *
 * No necesita internet ni archivos de audio. Si el celular no tiene voz en español,
 * la intro sigue igual, sin sonido.
 */
class Narrador(context: Context) {

    private var listo = false
    private var sinVoz = false
    private var pendiente: String? = null
    private var terminoActual: CompletableDeferred<Unit>? = null

    private lateinit var tts: TextToSpeech

    init {
        tts = TextToSpeech(context.applicationContext) { estado -> alIniciar(estado) }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) { terminoActual?.complete(Unit) }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { terminoActual?.complete(Unit) }
        })
    }

    private fun alIniciar(estado: Int) {
        // Español de Argentina; si no está, cualquier español.
        val idioma = if (estado == TextToSpeech.SUCCESS) {
            listOf(Locale("es", "AR"), Locale("es", "US"), Locale("es"))
                .firstOrNull { tts.isLanguageAvailable(it) >= TextToSpeech.LANG_AVAILABLE }
        } else null
        if (idioma == null) {
            // Sin voz en español: la animación sigue sin sonido.
            sinVoz = true
            terminoActual?.complete(Unit)
            return
        }
        tts.language = idioma
        tts.setSpeechRate(0.95f)
        listo = true
        pendiente?.let { hablarAhora(it) }
        pendiente = null
    }

    /**
     * Dice el texto y espera a que termine (como máximo [maxMs]).
     * Así cada mensaje de la animación queda en pantalla mientras se escucha.
     */
    suspend fun decir(texto: String, maxMs: Long = 6_000) {
        val termino = CompletableDeferred<Unit>()
        terminoActual = termino
        when {
            sinVoz -> return
            listo -> hablarAhora(texto)
            else -> pendiente = texto
        }
        withTimeoutOrNull(maxMs) { termino.await() }
    }

    private fun hablarAhora(texto: String) {
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "intro")
    }

    fun callar() {
        pendiente = null
        tts.stop()
        terminoActual?.complete(Unit)
    }

    fun liberar() {
        callar()
        tts.shutdown()
    }
}

/**
 * Crea el narrador para una pantalla.
 * Ciclo de vida: se calla en onPause y se libera (shutdown) cuando la pantalla deja de mostrarse.
 */
@Composable
fun rememberNarrador(): Narrador {
    val context = LocalContext.current
    val narrador = remember { Narrador(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_PAUSE) narrador.callar()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            narrador.liberar()
        }
    }
    return narrador
}
