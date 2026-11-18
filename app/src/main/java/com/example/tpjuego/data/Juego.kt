package com.example.tpjuego.data

/** Los tres juegos de la app. [mayorEsMejor] indica cómo se compara el resultado. */
enum class Juego(val titulo: String, val mayorEsMejor: Boolean) {
    MITO_O_REALIDAD("¿Mito o Realidad?", mayorEsMejor = true),   // más aciertos es mejor
    MEMOTEST("Memotest", mayorEsMejor = false),                  // menos intentos es mejor
    LA_PREVIA("La Previa", mayorEsMejor = true);                 // decisiones tomadas

    /** true si [nuevo] supera a [actual] según el criterio del juego (null = no había marca). */
    fun esMejor(nuevo: Int, actual: Int?): Boolean = when {
        actual == null -> true
        mayorEsMejor -> nuevo > actual
        else -> nuevo < actual
    }

    companion object {
        fun desdeNombre(nombre: String?): Juego? = entries.firstOrNull { it.name == nombre }
    }
}

/** Resumen de un juego para un usuario (lo que muestra "Mi resumen"). */
data class ResumenJuego(
    val juego: Juego,
    val partidas: Int = 0,
    val mejor: Int? = null,
    val ultimo: Int? = null,
    val ultimaVezMillis: Long? = null
) {
    /** Suma una partida terminada y actualiza el mejor resultado si corresponde. */
    fun registrar(resultado: Int, ahoraMillis: Long): ResumenJuego = copy(
        partidas = partidas + 1,
        mejor = if (juego.esMejor(resultado, mejor)) resultado else mejor,
        ultimo = resultado,
        ultimaVezMillis = ahoraMillis
    )
}

object Resumen {
    /**
     * Arma el resumen de cada juego a partir de las partidas guardadas en Room.
     * Es Kotlin puro: se prueba con tests unitarios sin Android.
     */
    fun armar(partidas: List<com.example.tpjuego.data.local.PartidaEntity>): List<ResumenJuego> =
        Juego.entries.map { juego ->
            partidas
                .filter { it.juego == juego.name }
                .sortedBy { it.fechaMillis }
                .fold(ResumenJuego(juego)) { r, p -> r.registrar(p.resultado, p.fechaMillis) }
        }
}
