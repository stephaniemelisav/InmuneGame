package com.example.tpjuego

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge

/**
 * Activity base de la app. Todas las Activities heredan de esta clase.
 *
 * - Configura la pantalla completa con íconos claros (el fondo de la app siempre es oscuro).
 * - Registra en el Logcat cada paso del ciclo de vida, con el filtro "CicloDeVida".
 *   Así se puede ver en vivo cómo se crea, pausa y destruye cada Activity
 *   al entrar y salir de los juegos.
 */
abstract class BaseActivity : ComponentActivity() {

    private val nombre: String get() = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        // savedInstanceState != null significa que Android recreó la Activity (por ejemplo, al rotar).
        log("onCreate" + if (savedInstanceState != null) " (recreada)" else "")
    }

    override fun onStart() {
        super.onStart()
        log("onStart: la Activity es visible")
    }

    override fun onResume() {
        super.onResume()
        log("onResume: el jugador puede interactuar")
    }

    override fun onPause() {
        super.onPause()
        log("onPause: la Activity pierde el foco")
    }

    override fun onStop() {
        super.onStop()
        log("onStop: la Activity ya no es visible")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart: vuelve a primer plano")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy" + if (isFinishing) ": se cerró (finish)" else ": se destruye para recrearse")
    }

    private fun log(mensaje: String) = Log.d(TAG, "$nombre → $mensaje")

    companion object {
        const val TAG = "CicloDeVida"
    }
}
