package com.example.tpjuego.game

import kotlin.math.abs
import kotlin.math.sqrt

/** Lo que detecta el acelerómetro y usa el juego. */
enum class Movimiento { INCLINAR_DERECHA, INCLINAR_IZQUIERDA, SACUDIR }

/**
 * Interpreta las lecturas del acelerómetro (en m/s²) y avisa cuando hay un gesto.
 *
 * Es Kotlin puro: recibe los valores x, y, z que manda el sensor, así se prueba
 * con tests unitarios sin un celular.
 *
 *  - Inclinar: el eje x mide hacia dónde está caído el celular. Con el celular inclinado
 *    hacia la derecha, x es negativo; hacia la izquierda, positivo. Hay que volver a
 *    ponerlo derecho antes de cada inclinación (así no responde dos veces).
 *  - Sacudir: la fuerza total supera bastante la gravedad (~9,8 m/s²).
 */
class DetectorMovimiento(
    private val umbralInclinacion: Float = 6.0f,
    private val umbralNeutro: Float = 2.5f,
    private val umbralSacudida: Float = 25f,
    private val esperaSacudidaMs: Long = 1000
) {
    // Arranca en false: el celular tiene que estar derecho antes de la primera inclinación.
    // Así, si en la pregunta anterior quedó inclinado, no responde solo la siguiente.
    private var listoParaInclinar = false
    private var ultimaSacudidaMs = -esperaSacudidaMs

    fun procesar(x: Float, y: Float, z: Float, ahoraMs: Long): Movimiento? {
        val fuerza = sqrt(x * x + y * y + z * z)
        if (fuerza > umbralSacudida && ahoraMs - ultimaSacudidaMs >= esperaSacudidaMs) {
            ultimaSacudidaMs = ahoraMs
            return Movimiento.SACUDIR
        }
        if (abs(x) < umbralNeutro) {
            listoParaInclinar = true
            return null
        }
        if (listoParaInclinar && abs(x) > umbralInclinacion) {
            listoParaInclinar = false
            return if (x < 0) Movimiento.INCLINAR_DERECHA else Movimiento.INCLINAR_IZQUIERDA
        }
        return null
    }
}
