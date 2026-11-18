package com.example.tpjuego.ui.multimedia

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tpjuego.game.DetectorMovimiento
import com.example.tpjuego.game.Movimiento

/**
 * Escucha el acelerómetro mientras la pantalla está en primer plano.
 *
 * Ciclo de vida: el sensor se registra en ON_RESUME y se libera en ON_PAUSE.
 * Así no gasta batería cuando la app está en segundo plano, y se libera del todo
 * cuando la pantalla deja de existir.
 *
 * @param activo si es false no se registra (por ejemplo, fuera de la fase de pregunta).
 */
@Composable
fun EscucharMovimiento(activo: Boolean = true, onMovimiento: (Movimiento) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val alMoverse by rememberUpdatedState(onMovimiento)

    DisposableEffect(lifecycleOwner, activo) {
        val manager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val acelerometro = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (!activo || manager == null || acelerometro == null) {
            // Sin acelerómetro (o desactivado): el juego se sigue usando con los botones.
            return@DisposableEffect onDispose { }
        }

        val detector = DetectorMovimiento()
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (x, y, z) = event.values
                detector.procesar(x, y, z, SystemClock.elapsedRealtime())?.let { alMoverse(it) }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
        }

        fun registrar() = manager.registerListener(listener, acelerometro, SensorManager.SENSOR_DELAY_GAME)
        fun liberar() = manager.unregisterListener(listener)

        val observer = LifecycleEventObserver { _, evento ->
            when (evento) {
                Lifecycle.Event.ON_RESUME -> registrar()
                Lifecycle.Event.ON_PAUSE -> liberar()
                else -> Unit
            }
        }
        // Si la pantalla ya está en primer plano, addObserver envía ON_RESUME enseguida.
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            liberar()
        }
    }
}
