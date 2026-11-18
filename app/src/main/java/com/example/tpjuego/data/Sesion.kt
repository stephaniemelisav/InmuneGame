package com.example.tpjuego.data

import android.content.Context

/**
 * Usuario con la sesión iniciada. Se guarda en SharedPreferences para que la sesión
 * siga abierta aunque Android cierre el proceso o se reinicie la app.
 */
object Sesion {
    private const val ARCHIVO = "sesion"
    private const val CLAVE = "usuario_actual"

    fun usuarioActual(context: Context): String? =
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).getString(CLAVE, null)

    fun iniciar(context: Context, usuario: String) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit().putString(CLAVE, usuario).apply()
    }

    fun cerrar(context: Context) {
        context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE).edit().remove(CLAVE).apply()
    }

    /** Para los datos que se guardan por usuario (historial, récord, resumen). */
    fun usuarioOInvitado(context: Context): String = usuarioActual(context) ?: "invitado"
}
