package com.example.tpjuego.ui.multimedia

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.tpjuego.R

/**
 * Efectos de respuesta del juego: sonidos cortos (res/raw) y vibración.
 *
 * Usa SoundPool, pensado para sonidos breves que se repiten (precarga los archivos en memoria).
 * Los recursos se liberan con [liberar] cuando la pantalla sale de la composición,
 * es decir cuando la Activity del juego se destruye (ver [rememberEfectos]).
 */
class Efectos(context: Context) {

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(3)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val acierto = soundPool.load(context, R.raw.sonido_acierto, 1)
    private val error = soundPool.load(context, R.raw.sonido_error, 1)
    private val fin = soundPool.load(context, R.raw.sonido_fin, 1)
    private val tic = soundPool.load(context, R.raw.sonido_tic, 1)

    private val vibrador: Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    fun sonarAcierto() = sonar(acierto)
    fun sonarError() = sonar(error)
    fun sonarFin() = sonar(fin)

    /** Tic del reloj. En los últimos segundos suena más agudo para avisar que se termina. */
    fun sonarTic(urgente: Boolean) = sonar(tic, velocidad = if (urgente) 1.5f else 1f)

    /** Vibración corta: se usa cuando la respuesta es incorrecta. */
    fun vibrar(duracionMs: Long = 250) {
        val v = vibrador ?: return
        if (!v.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duracionMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(duracionMs)
        }
    }

    private fun sonar(id: Int, velocidad: Float = 1f) {
        soundPool.play(id, 1f, 1f, 1, 0, velocidad)
    }

    fun liberar() {
        soundPool.release()
        vibrador?.cancel()
    }
}

/** Crea los efectos una vez por pantalla y los libera cuando la pantalla deja de existir. */
@Composable
fun rememberEfectos(): Efectos {
    val context = LocalContext.current
    val efectos = remember { Efectos(context.applicationContext) }
    DisposableEffect(Unit) {
        onDispose { efectos.liberar() }
    }
    return efectos
}
