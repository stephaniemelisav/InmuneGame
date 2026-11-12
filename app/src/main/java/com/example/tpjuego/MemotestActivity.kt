package com.example.tpjuego

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.tpjuego.ui.screens.MemotestScreen
import com.example.tpjuego.ui.screens.PortadaMemotestScreen
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity del Memotest (juego desarrollado por Enzo).
 * Primero muestra la portada; con "Comencemos" pasa al juego.
 * "Volver al menú" y "Volver" llaman a finish() y se vuelve a MainActivity.
 */
class MemotestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPJuegoTheme {
                var mostrarPortada by rememberSaveable { mutableStateOf(true) }

                if (mostrarPortada) {
                    PortadaMemotestScreen(
                        onComenzar = { mostrarPortada = false },
                        onVolver = { finish() }
                    )
                } else {
                    MemotestScreen(onVolver = { finish() })
                }
            }
        }
    }
}