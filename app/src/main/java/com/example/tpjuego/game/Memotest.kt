package com.example.tpjuego.game

import com.example.tpjuego.data.MemotestTemas
import com.example.tpjuego.data.Tema

/** Una carta del tablero. Las dos cartas de una pareja tienen el mismo [parejaId] y el mismo texto. */
data class Carta(
    val parejaId: Int,
    val texto: String,
    val volteada: Boolean = false,
    val encontrada: Boolean = false
)

/** Qué pasó con el último toque. La pantalla lo usa para el sonido y la vibración. */
enum class ResultadoToque { NADA, PRIMERA_CARTA, PAR_ENCONTRADO, NO_COINCIDE }

/**
 * Estado de una partida del Memotest, con las mismas reglas que programó Enzo:
 *  1. Se dan vuelta dos cartas; cada par de cartas cuenta un intento.
 *  2. Si son pareja quedan encontradas y se abre la nota de esa pareja.
 *     Si no, quedan visibles hasta que se llame a [tapar] (el ViewModel espera 1 segundo).
 *  3. Al cerrar la nota de la última pareja, se pasa a la pantalla final.
 *
 * Es Kotlin puro e inmutable (como Partida e Historia): se prueba con tests y vive en el
 * ViewModel, así la partida no se pierde al rotar el celular.
 */
data class Memotest(
    val temas: List<Tema> = MemotestTemas.temas,
    val cartas: List<Carta> = generarCartas(temas),
    val seleccion: List<Int> = emptyList(),
    val intentos: Int = 0,
    /** Parejas encontradas, en el orden en que se encontraron. */
    val notas: List<Int> = emptyList(),
    /** Pareja cuya nota se está leyendo, o null. */
    val notaAbierta: Int? = null,
    val mostrarFinal: Boolean = false,
    val ultimoToque: ResultadoToque = ResultadoToque.NADA
) {
    val todasEncontradas: Boolean get() = cartas.isNotEmpty() && cartas.all { it.encontrada }
    val esperandoTapar: Boolean get() = seleccion.size == 2

    fun tocar(indice: Int): Memotest {
        val carta = cartas.getOrNull(indice)
        // Igual que en la versión de Enzo: no se puede tocar con una nota abierta,
        // con dos cartas visibles o sobre una carta ya visible.
        if (carta == null || notaAbierta != null || mostrarFinal || esperandoTapar ||
            carta.volteada || carta.encontrada
        ) return copy(ultimoToque = ResultadoToque.NADA)

        val volteadas = cartas.toMutableList().also { it[indice] = carta.copy(volteada = true) }
        if (seleccion.isEmpty()) {
            return copy(cartas = volteadas, seleccion = listOf(indice), ultimoToque = ResultadoToque.PRIMERA_CARTA)
        }

        val otra = seleccion.first()
        return if (volteadas[otra].parejaId == carta.parejaId) {
            volteadas[otra] = volteadas[otra].copy(encontrada = true)
            volteadas[indice] = volteadas[indice].copy(encontrada = true)
            copy(
                cartas = volteadas,
                seleccion = emptyList(),
                intentos = intentos + 1,
                notas = notas + carta.parejaId,
                notaAbierta = carta.parejaId,
                ultimoToque = ResultadoToque.PAR_ENCONTRADO
            )
        } else {
            copy(
                cartas = volteadas,
                seleccion = listOf(otra, indice),
                intentos = intentos + 1,
                ultimoToque = ResultadoToque.NO_COINCIDE
            )
        }
    }

    /** Vuelve a tapar las dos cartas que no coincidieron. */
    fun tapar(): Memotest {
        if (!esperandoTapar) return this
        val tapadas = cartas.toMutableList().also { lista ->
            seleccion.forEach { lista[it] = lista[it].copy(volteada = false) }
        }
        return copy(cartas = tapadas, seleccion = emptyList(), ultimoToque = ResultadoToque.NADA)
    }

    /** Vuelve a abrir la nota de una pareja ya encontrada. */
    fun abrirNota(id: Int): Memotest =
        if (notaAbierta == null && id in notas) copy(notaAbierta = id) else this

    /** Cierra la nota; si era la última pareja, pasa a la pantalla final (una sola vez). */
    fun cerrarNota(): Memotest {
        if (notaAbierta == null) return this
        return copy(notaAbierta = null, mostrarFinal = mostrarFinal || todasEncontradas)
    }

    companion object {
        fun generarCartas(temas: List<Tema> = MemotestTemas.temas): List<Carta> =
            temas.flatMapIndexed { i, tema -> listOf(Carta(i, tema.nombre), Carta(i, tema.nombre)) }
                .shuffled()
    }
}
