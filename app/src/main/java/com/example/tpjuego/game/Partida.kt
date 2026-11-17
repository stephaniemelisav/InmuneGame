package com.example.tpjuego.game

import com.example.tpjuego.data.Afirmacion

/**
 * Lo que respondió la persona en una pregunta. Sirve para el resumen final.
 * [eligioRealidad] es null si se le acabó el tiempo sin responder (cuenta como error).
 */
data class RespuestaDada(
    val afirmacion: Afirmacion,
    val eligioRealidad: Boolean?
) {
    val sinTiempo: Boolean get() = eligioRealidad == null
    val acerto: Boolean get() = eligioRealidad == afirmacion.esRealidad
}

/**
 * Estado de una partida de ¿Mito o Realidad?
 *
 * Es Kotlin puro (sin Android), así la lógica se puede probar con tests unitarios.
 * Cada acción devuelve un estado nuevo en lugar de modificar el actual.
 *
 * Fases de cada pregunta:
 *  - respondida == false -> se muestra la afirmación con Verdadero / Falso y el reloj
 *  - respondida == true  -> se muestra el resultado con la explicación
 *  - terminada == true  -> pantalla final con el puntaje y el resumen
 */
data class Partida(
    val afirmaciones: List<Afirmacion>,
    val indice: Int = 0,
    val respuestas: List<RespuestaDada> = emptyList(),
    val terminada: Boolean = false
) {
    init {
        require(afirmaciones.isNotEmpty()) { "La partida necesita al menos una afirmación" }
    }

    val actual: Afirmacion get() = afirmaciones[indice]
    val total: Int get() = afirmaciones.size
    val numeroPregunta: Int get() = indice + 1
    val esUltima: Boolean get() = indice == afirmaciones.lastIndex

    /** true cuando la pregunta actual ya tiene respuesta (o se le acabó el tiempo). */
    val respondida: Boolean get() = respuestas.size > indice

    /** Lo que eligió en la pregunta actual (true = Verdadero), o null si no respondió. */
    val respuesta: Boolean? get() = respuestas.getOrNull(indice)?.eligioRealidad

    /** true si en la pregunta actual se acabó el tiempo. */
    val sinTiempo: Boolean get() = respuestas.getOrNull(indice)?.sinTiempo == true

    /** null mientras no respondió la pregunta actual. */
    val acerto: Boolean? get() = respuestas.getOrNull(indice)?.acerto

    val aciertos: Int get() = respuestas.count { it.acerto }
    val errores: Int get() = respuestas.size - aciertos

    /** @param eligioRealidad true si tocó Verdadero, false si tocó Falso. */
    fun responder(eligioRealidad: Boolean): Partida {
        // Ignora toques repetidos: solo cuenta la primera respuesta de cada pregunta.
        if (terminada || respondida) return this
        return copy(respuestas = respuestas + RespuestaDada(actual, eligioRealidad))
    }

    /** Se acabó el tiempo de la pregunta actual: cuenta como error. */
    fun sinTiempo(): Partida {
        if (terminada || respondida) return this
        return copy(respuestas = respuestas + RespuestaDada(actual, null))
    }

    /** Pasa a la próxima pregunta, o termina si era la última. */
    fun siguiente(): Partida {
        if (terminada || !respondida) return this
        return if (esUltima) copy(terminada = true)
        else copy(indice = indice + 1)
    }
}
