package com.example.tpjuego.data

import android.content.Context

/**
 * Historial de partidas de ¿Mito o Realidad? guardado en el celular (SharedPreferences).
 * Se guarda por usuario. Solo fecha y puntaje, sin datos personales.
 */
class HistorialPartidas(context: Context, usuario: String) {

    // Un archivo por usuario: cada uno ve solo sus partidas.
    private val prefs = context.getSharedPreferences("${ARCHIVO}_$usuario", Context.MODE_PRIVATE)

    /** Las partidas guardadas, de la más reciente a la más vieja. */
    fun leer(): List<ResultadoGuardado> =
        HistorialCodec.decodificar(prefs.getString(CLAVE, "") ?: "")

    /** Agrega un resultado al principio y devuelve el historial actualizado. */
    fun guardar(resultado: ResultadoGuardado): List<ResultadoGuardado> {
        val actualizado = (listOf(resultado) + leer()).take(MAXIMO)
        prefs.edit().putString(CLAVE, HistorialCodec.codificar(actualizado)).apply()
        return actualizado
    }

    private companion object {
        const val ARCHIVO = "historial_mito_o_realidad"
        const val CLAVE = "partidas"
        const val MAXIMO = 10
    }
}
