package com.example.tpjuego.data

/** Una fila del ranking: posición, jugador (su usuario) y su mejor resultado. */
data class FilaRanking(val posicion: Int, val usuario: String, val resultado: Int)

/** Lo que muestra la pantalla de Ranking. */
sealed class EstadoRanking {
    data object Cargando : EstadoRanking()
    /** Falta app/google-services.json. */
    data object NoConfigurado : EstadoRanking()
    /** [sinConexion] = true si los datos vienen de la caché del celular (no hay internet). */
    data class Listo(val filas: List<FilaRanking>, val sinConexion: Boolean = false) : EstadoRanking()
    data class Error(val mensaje: String) : EstadoRanking()
}

object Ranking {
    /**
     * Arma el ranking a partir de (usuario, resultado) de todas las partidas de un juego:
     * se queda con el mejor resultado de cada jugador y los ordena según el criterio del juego.
     * Kotlin puro: se prueba con tests unitarios.
     */
    fun armar(partidas: List<Pair<String, Int>>, juego: Juego, cantidad: Int = 10): List<FilaRanking> {
        val mejorDeCadaUno = partidas
            .groupBy({ it.first }, { it.second })
            .map { (usuario, resultados) ->
                usuario to (if (juego.mayorEsMejor) resultados.max() else resultados.min())
            }
        val ordenados = if (juego.mayorEsMejor) {
            mejorDeCadaUno.sortedWith(compareByDescending<Pair<String, Int>> { it.second }.thenBy { it.first })
        } else {
            mejorDeCadaUno.sortedWith(compareBy<Pair<String, Int>> { it.second }.thenBy { it.first })
        }
        return ordenados.take(cantidad).mapIndexed { i, (usuario, resultado) ->
            FilaRanking(posicion = i + 1, usuario = usuario, resultado = resultado)
        }
    }
}
