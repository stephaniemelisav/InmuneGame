package com.example.tpjuego.data

/** Resultado de una partida terminada de ¿Mito o Realidad?, para el historial de la pantalla final. */
data class ResultadoGuardado(
    val fechaMillis: Long,
    val aciertos: Int,
    val total: Int
) {
    val errores: Int get() = total - aciertos
}
