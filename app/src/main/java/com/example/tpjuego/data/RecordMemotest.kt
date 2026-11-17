package com.example.tpjuego.data

import android.content.Context

/**
 * Récord del Memotest guardado en el celular (SharedPreferences):
 * la menor cantidad de intentos con la que se completó el tablero.
 */
class RecordMemotest(context: Context, usuario: String) {

    // Un récord por usuario.
    private val prefs = context.getSharedPreferences("${ARCHIVO}_$usuario", Context.MODE_PRIVATE)

    /** null si todavía no se terminó ninguna partida. */
    fun leer(): Int? = prefs.getInt(CLAVE, SIN_RECORD).takeIf { it != SIN_RECORD }

    /**
     * Guarda [intentos] si es mejor que el récord actual.
     * @return true si es un récord nuevo.
     */
    fun guardarSiEsMejor(intentos: Int): Boolean {
        val actual = leer()
        if (!esMejor(intentos, actual)) return false
        prefs.edit().putInt(CLAVE, intentos).apply()
        return true
    }

    companion object {
        private const val ARCHIVO = "record_memotest"
        private const val CLAVE = "menos_intentos"
        private const val SIN_RECORD = -1

        /** Separado de Android para poder probarlo con tests unitarios. */
        fun esMejor(intentos: Int, recordActual: Int?): Boolean =
            recordActual == null || intentos < recordActual
    }
}
