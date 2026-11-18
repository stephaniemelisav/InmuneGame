package com.example.tpjuego.ui.components

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

/**
 * Reproduce una música en bucle mientras la pantalla que la llama está visible.
 * - Se pausa si la app pasa a segundo plano y se retoma al volver.
 * - Se libera sola cuando la pantalla desaparece.
 * Uso: MusicaDeFondo(R.raw.music3)
 */
@Composable
fun MusicaDeFondo(recurso: Int, volumen: Float = 0.4f, enBucle: Boolean = true) {
    val context = LocalContext.current
    val musica = remember(recurso) {
        MediaPlayer.create(context, recurso)?.apply {
            isLooping = enBucle
            setVolume(volumen, volumen)
        }
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, musica) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> musica?.start()
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
}