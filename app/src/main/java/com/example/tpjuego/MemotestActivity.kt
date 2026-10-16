package com.example.tpjuego

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.ui.screens.MemotestScreen
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity del Memotest (juego desarrollado por Enzo).
 * "Terminar y volver al menú" llama a finish() y se vuelve a MainActivity.
 */
class MemotestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPJuegoTheme {
                MemotestScreen(onVolver = { finish() })
            }
        }
    }
}
