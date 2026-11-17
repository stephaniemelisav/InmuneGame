package com.example.tpjuego.game

import com.example.tpjuego.data.Escena
import com.example.tpjuego.data.LaPreviaHistoria
import com.example.tpjuego.data.Opcion
import com.example.tpjuego.data.Recurso

/** Una decisión tomada: en qué escena y qué se eligió. Sirve para "Tu recorrido". */
data class Paso(val escena: Escena, val opcion: Opcion)

/**
 * Estado de una partida de La Previa.
 *
 * Es Kotlin puro (sin Android), igual que [Partida], así se prueba con tests unitarios.
 * Cada acción devuelve un estado nuevo en lugar de modificar el actual.
 *
 * Fases:
 *  - elegida == null    -> se muestra la escena con sus opciones
 *  - elegida != null    -> se muestra la consecuencia de lo que se eligió
 *  - terminada == true  -> pantalla final con el recorrido y los recursos
 */
data class Historia(
    val escenas: Map<String, Escena> = LaPreviaHistoria.escenas,
    val actualId: String = LaPreviaHistoria.INICIO,
    val elegida: Opcion? = null,
    val recorrido: List<Paso> = emptyList(),
    val terminada: Boolean = false,
    val totalEscenas: Int = LaPreviaHistoria.TOTAL_ESCENAS
) {
    init {
        require(actualId in escenas) { "No existe la escena '$actualId'" }
    }

    val actual: Escena get() = escenas.getValue(actualId)

    /** Recursos juntados en el camino, sin repetir, más el que se suma siempre al final. */
    val recursos: List<Recurso>
        get() = (recorrido.mapNotNull { it.opcion.recurso } + LaPreviaHistoria.recursoFinal).distinct()

    /** @param indice posición de la opción elegida en la escena actual. */
    fun elegir(indice: Int): Historia {
        // Ignora toques repetidos y opciones que no existen.
        if (terminada || elegida != null) return this
        val opcion = actual.opciones.getOrNull(indice) ?: return this
        return copy(elegida = opcion, recorrido = recorrido + Paso(actual, opcion))
    }

    /** Después de leer la consecuencia: pasa a la escena siguiente, o termina. */
    fun seguir(): Historia {
        val opcion = elegida ?: return this
        if (terminada) return this
        val siguiente = opcion.siguiente ?: return copy(terminada = true)
        return copy(actualId = siguiente, elegida = null)
    }
}
