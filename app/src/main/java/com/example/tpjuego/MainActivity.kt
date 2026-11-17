package com.example.tpjuego

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.data.Sesion
import com.example.tpjuego.ui.AppNavigation
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Activity principal: intro de concientización, menú de juegos y resumen del usuario.
 * Cada juego se abre en su propia Activity con un Intent.
 */
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val usuario = Sesion.usuarioActual(this)
        if (usuario == null) {
            // Sin sesión iniciada no se puede entrar al menú.
            irAlLogin()
            return
        }
        setContent {
            TPJuegoTheme {
                AppNavigation(
                    usuario = usuario,
                    onAbrirMitoORealidad = {
                        startActivity(Intent(this, MitoORealidadActivity::class.java))
                    },
                    onAbrirMemotest = {
                        startActivity(Intent(this, MemotestActivity::class.java))
                    },
                    onAbrirLaPrevia = {
                        startActivity(Intent(this, LaPreviaActivity::class.java))
                    },
                    onCerrarSesion = {
                        Sesion.cerrar(this)
                        irAlLogin()
                    }
                )
            }
        }
    }

    private fun irAlLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
