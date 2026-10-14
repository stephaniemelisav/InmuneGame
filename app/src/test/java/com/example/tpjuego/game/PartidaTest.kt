package com.example.tpjuego.game

import com.example.tpjuego.data.Afirmacion
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.HistorialCodec
import com.example.tpjuego.data.ResultadoGuardado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PartidaTest {

    private val mito = Afirmacion("Mito", esRealidad = false, explicacion = "", fuente = "")
    private val realidad = Afirmacion("Realidad", esRealidad = true, explicacion = "", fuente = "")

    @Test
    fun acertarSumaUnPunto() {
        val p = Partida(listOf(mito, realidad)).responder(eligioRealidad = false)
        assertEquals(1, p.aciertos)
        assertEquals(true, p.acerto)
    }

    @Test
    fun equivocarseNoSumaPeroMuestraResultado() {
        val p = Partida(listOf(mito, realidad)).responder(eligioRealidad = true)
        assertEquals(0, p.aciertos)
        assertEquals(false, p.acerto)
    }

    @Test
    fun soloCuentaLaPrimeraRespuesta() {
        val p = Partida(listOf(mito)).responder(false).responder(false)
        assertEquals(1, p.aciertos)
    }

    @Test
    fun noAvanzaSinResponder() {
        val p = Partida(listOf(mito, realidad))
        assertEquals(p, p.siguiente())
    }

    @Test
    fun siguientePasaALaProximaPregunta() {
        val p = Partida(listOf(mito, realidad)).responder(false).siguiente()
        assertEquals(2, p.numeroPregunta)
        assertNull(p.respuesta)
        assertFalse(p.terminada)
    }

    @Test
    fun terminaDespuesDeLaUltima() {
        val p = Partida(listOf(mito, realidad))
            .responder(false).siguiente()
            .responder(false).siguiente()
        assertTrue(p.terminada)
        assertEquals(1, p.aciertos)
        assertEquals(2, p.total)
    }

    @Test
    fun cadaPartidaTieneCincoPreguntasDistintas() {
        val afirmaciones = AfirmacionesRepositorio.paraPartida()
        assertEquals(5, afirmaciones.size)
        assertEquals(5, afirmaciones.distinct().size)
    }

    @Test
    fun cuentaErroresYGuardaElResumen() {
        val p = Partida(listOf(mito, realidad, mito))
            .responder(true).siguiente()   // error
            .responder(true).siguiente()   // acierto
            .responder(true).siguiente()   // error
        assertEquals(1, p.aciertos)
        assertEquals(2, p.errores)
        assertEquals(listOf(false, true, false), p.respuestas.map { it.acerto })
    }

    @Test
    fun historialSeGuardaYSeLeeIgual() {
        val lista = listOf(ResultadoGuardado(1_000L, 4, 5), ResultadoGuardado(500L, 2, 5))
        assertEquals(lista, HistorialCodec.decodificar(HistorialCodec.codificar(lista)))
        assertEquals(emptyList<ResultadoGuardado>(), HistorialCodec.decodificar(""))
    }
}
