package com.example.tpjuego.game

import com.example.tpjuego.data.Tema
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MemotestTest {

    // Tablero fijo (sin mezclar): 0 y 1 son la pareja A, 2 y 3 la pareja B.
    private val temas = listOf(Tema("A", "nota A"), Tema("B", "nota B"))
    private fun tablero() = Memotest(
        temas = temas,
        cartas = listOf(Carta(0, "A"), Carta(0, "A"), Carta(1, "B"), Carta(1, "B"))
    )

    @Test
    fun generaDosCartasIgualesPorPareja() {
        val cartas = Memotest.generarCartas()
        assertEquals(12, cartas.size)
        assertTrue(cartas.groupBy { it.parejaId }.values.all { it.size == 2 && it[0].texto == it[1].texto })
    }

    @Test
    fun encontrarUnaParejaAbreSuNota() {
        val m = tablero().tocar(0).tocar(1)
        assertEquals(ResultadoToque.PAR_ENCONTRADO, m.ultimoToque)
        assertEquals(1, m.intentos)
        assertEquals(listOf(0), m.notas)
        assertEquals(0, m.notaAbierta)
    }

    @Test
    fun conLaNotaAbiertaNoSePuedenTocarCartas() {
        val m = tablero().tocar(0).tocar(1).tocar(2)
        assertEquals(ResultadoToque.NADA, m.ultimoToque)
        assertFalse(m.cartas[2].volteada)
    }

    @Test
    fun siNoCoincidenQuedanVisiblesHastaTapar() {
        val m = tablero().tocar(0).tocar(2)
        assertEquals(ResultadoToque.NO_COINCIDE, m.ultimoToque)
        assertTrue(m.esperandoTapar)
        val tapado = m.tapar()
        assertFalse(tapado.cartas[0].volteada)
        assertFalse(tapado.cartas[2].volteada)
        assertEquals(1, tapado.intentos)
    }

    @Test
    fun alCerrarLaUltimaNotaPasaALaPantallaFinal() {
        var m = tablero().tocar(0).tocar(1).cerrarNota()
        assertFalse(m.mostrarFinal)
        m = m.tocar(2).tocar(3)
        assertFalse(m.mostrarFinal) // todavía está leyendo la última nota
        m = m.cerrarNota()
        assertTrue(m.mostrarFinal)
        assertNull(m.notaAbierta)
        assertEquals(2, m.intentos)
    }

    @Test
    fun sePuedeVolverALeerUnaNota() {
        val m = tablero().tocar(0).tocar(1).cerrarNota().abrirNota(0)
        assertEquals(0, m.notaAbierta)
        // Una pareja no encontrada no se puede abrir.
        assertNull(tablero().abrirNota(1).notaAbierta)
    }
}
