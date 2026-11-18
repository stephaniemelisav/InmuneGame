package com.example.tpjuego

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.tpjuego.data.Sesion
import com.example.tpjuego.ui.screens.LoginScreen
import com.example.tpjuego.ui.theme.TPJuegoTheme

/**
 * Primera Activity de la app (LAUNCHER): pide usuario y contraseña.
 * Si ya hay una sesión iniciada, pasa directo al menú.
 */
class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Sesion.usuarioActual(this) != null) {
            irAlMenu()
            return
        }
        setContent {
            TPJuegoTheme {
                LoginScreen(onIngreso = { irAlMenu() })
            }
        }
    }

    /** Abre el menú y cierra esta Activity, así "Atrás" desde el menú no vuelve al login. */
    private fun irAlMenu() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
