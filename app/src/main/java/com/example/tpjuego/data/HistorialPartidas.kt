package com.example.tpjuego.data

import android.content.Context

/**
 * Historial de partidas de ¿Mito o Realidad? guardado en el celular (SharedPreferences).
 * No pide registro ni datos personales: solo fecha y puntaje.
 */
class HistorialPartidas(context: Context) {

    private val prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

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
