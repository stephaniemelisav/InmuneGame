package com.example.tpjuego

import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.ui.screens.LaPreviaScreen
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity de La Previa (modo historia).
 * "Terminar y volver al menú" llama a finish() y se vuelve a MainActivity.
 */
class LaPreviaActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPJuegoTheme {
                LaPreviaScreen(onTerminar = { finish() })
            }
        }
    }
}
