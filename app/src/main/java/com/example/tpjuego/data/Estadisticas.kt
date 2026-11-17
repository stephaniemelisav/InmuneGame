package com.example.tpjuego.data

import android.content.Context

/** Los tres juegos de la app. [mayorEsMejor] indica cómo se compara el resultado. */
enum class Juego(val titulo: String, val mayorEsMejor: Boolean) {
    MITO_O_REALIDAD("¿Mito o Realidad?", mayorEsMejor = true),   // más aciertos es mejor
    MEMOTEST("Memotest", mayorEsMejor = false),                  // menos intentos es mejor
    LA_PREVIA("La Previa", mayorEsMejor = true)                  // escenas recorridas
}

/** Resumen de un juego para un usuario. */
data class ResumenJuego(
    val juego: Juego,
    val partidas: Int = 0,
    val mejor: Int? = null,
    val ultimo: Int? = null,
    val ultimaVezMillis: Long? = null
) {
    /** Suma una partida terminada y actualiza el mejor resultado si corresponde. */
    fun registrar(resultado: Int, ahoraMillis: Long): ResumenJuego {
        val nuevoMejor = when {
            mejor == null -> resultado
            juego.mayorEsMejor -> maxOf(mejor, resultado)
            else -> minOf(mejor, resultado)
        }
        return copy(partidas = partidas + 1, mejor = nuevoMejor, ultimo = resultado, ultimaVezMillis = ahoraMillis)
    }
}

/**
 * Estadísticas de cada usuario por juego, guardadas en SharedPreferences.
 * Cada usuario tiene su propio archivo, así los resúmenes no se mezclan.
 */
class Estadisticas(context: Context, usuario: String) {

    private val prefs = context.getSharedPreferences("estadisticas_$usuario", Context.MODE_PRIVATE)

    fun leer(juego: Juego): ResumenJuego {
        val k = juego.name
        if (!prefs.contains("${k}_partidas")) return ResumenJuego(juego)
        return ResumenJuego(
            juego = juego,
            partidas = prefs.getInt("${k}_partidas", 0),
            mejor = prefs.getInt("${k}_mejor", 0),
            ultimo = prefs.getInt("${k}_ultimo", 0),
            ultimaVezMillis = prefs.getLong("${k}_fecha", 0L)
        )
    }

    fun leerTodo(): List<ResumenJuego> = Juego.entries.map { leer(it) }

    fun registrar(juego: Juego, resultado: Int) {
        val r = leer(juego).registrar(resultado, System.currentTimeMillis())
        val k = juego.name
        prefs.edit()
            .putInt("${k}_partidas", r.partidas)
            .putInt("${k}_mejor", r.mejor ?: resultado)
            .putInt("${k}_ultimo", r.ultimo ?: resultado)
            .putLong("${k}_fecha", r.ultimaVezMillis ?: 0L)
            .apply()
    }

    companion object {
        /**
         * Registra una partida terminada del usuario con la sesión iniciada:
         * en el celular (resumen) y en la base de datos externa (Firestore).
         */
        fun registrarPartida(context: Context, juego: Juego, resultado: Int) {
            val usuario = Sesion.usuarioOInvitado(context)
            Estadisticas(context, usuario).registrar(juego, resultado)
            PartidasRemotas.guardar(context, usuario, juego, resultado)
        }
    }
}
