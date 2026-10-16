package com.example.tpjuego

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.ui.MitoORealidadNavigation
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity del juego ¿Mito o Realidad?: portada, cómo se juega, preguntas y pantalla final.
 *
 * "Terminar y volver al menú" llama a finish(): esta Activity se destruye
 * y Android vuelve sola a MainActivity, que quedó abajo en la pila.
 */
class MitoORealidadActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPJuegoTheme {
                MitoORealidadNavigation(onTerminar = { finish() })
            }
        }
    }
}
