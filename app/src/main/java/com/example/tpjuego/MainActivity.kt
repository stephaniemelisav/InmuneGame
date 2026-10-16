package com.example.tpjuego

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.ui.AppNavigation
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity principal: intro de concientización y menú de juegos.
 * Cada juego se abre en su propia Activity con un Intent.
 */
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TPJuegoTheme {
                AppNavigation(
                    onAbrirMitoORealidad = {
                        startActivity(Intent(this, MitoORealidadActivity::class.java))
                    },
                    onAbrirMemotest = {
                        startActivity(Intent(this, MemotestActivity::class.java))
                    },
                    onAbrirLaPrevia = {
                        startActivity(Intent(this, LaPreviaActivity::class.java))
                    }
                )
            }
        }
    }
}
