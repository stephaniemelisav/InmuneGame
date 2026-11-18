package com.example.tpjuego.data

import com.example.tpjuego.data.local.PartidaEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ResumenYRankingTest {

    private fun partida(usuario: String, juego: Juego, resultado: Int, fecha: Long) =
        PartidaEntity(usuario = usuario, juego = juego.name, resultado = resultado, fechaMillis = fecha)

    @Test
    fun enMitoGanaElMayorPuntaje() {
        val r = ResumenJuego(Juego.MITO_O_REALIDAD).registrar(3, 1).registrar(5, 2).registrar(2, 3)
        assertEquals(3, r.partidas)
        assertEquals(5, r.mejor)
        assertEquals(2, r.ultimo)
        assertEquals(3L, r.ultimaVezMillis)
    }

    @Test
    fun enMemotestGananLosMenosIntentos() {
        val r = ResumenJuego(Juego.MEMOTEST).registrar(12, 1).registrar(8, 2).registrar(10, 3)
        assertEquals(8, r.mejor)
    }

    @Test
    fun criterioDeRecord() {
        assertTrue(Juego.MEMOTEST.esMejor(10, null))
        assertTrue(Juego.MEMOTEST.esMejor(7, 8))
        assertFalse(Juego.MEMOTEST.esMejor(8, 8))
        assertTrue(Juego.MITO_O_REALIDAD.esMejor(5, 4))
    }

    @Test
    fun resumenDesdeLasFilasDeRoom() {
        val filas = listOf(
            partida("vale", Juego.MITO_O_REALIDAD, 4, 20),
            partida("vale", Juego.MITO_O_REALIDAD, 2, 10),
            partida("vale", Juego.MEMOTEST, 9, 30)
        )
        val resumen = Resumen.armar(filas).associateBy { it.juego }
        assertEquals(2, resumen.getValue(Juego.MITO_O_REALIDAD).partidas)
        assertEquals(4, resumen.getValue(Juego.MITO_O_REALIDAD).mejor)
        assertEquals(4, resumen.getValue(Juego.MITO_O_REALIDAD).ultimo) // la más nueva
        assertEquals(1, resumen.getValue(Juego.MEMOTEST).partidas)
        assertEquals(0, resumen.getValue(Juego.LA_PREVIA).partidas)
        assertNull(resumen.getValue(Juego.LA_PREVIA).mejor)
    }

    @Test
    fun rankingDeMitoOrdenaDeMayorAMenorConElMejorDeCadaJugador() {
        val partidas = listOf("ana" to 3, "beto" to 5, "ana" to 4, "caro" to 2)
        val ranking = Ranking.armar(partidas, Juego.MITO_O_REALIDAD)
        assertEquals(listOf("beto", "ana", "caro"), ranking.map { it.usuario })
        assertEquals(listOf(5, 4, 2), ranking.map { it.resultado })
        assertEquals(listOf(1, 2, 3), ranking.map { it.posicion })
    }

    @Test
    fun rankingDeMemotestOrdenaDeMenorAMayor() {
        val partidas = listOf("ana" to 14, "beto" to 9, "ana" to 8)
        val ranking = Ranking.armar(partidas, Juego.MEMOTEST)
        assertEquals(listOf("ana" to 8, "beto" to 9), ranking.map { it.usuario to it.resultado })
    }

    @Test
    fun rankingRespetaLaCantidad() {
        val partidas = (1..20).map { "j$it" to it }
        assertEquals(10, Ranking.armar(partidas, Juego.MITO_O_REALIDAD).size)
    }
}
