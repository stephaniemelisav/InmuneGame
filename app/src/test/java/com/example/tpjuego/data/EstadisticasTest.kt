package com.example.tpjuego.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadisticasTest {

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
    fun recordDelMemotest() {
        assertTrue(RecordMemotest.esMejor(10, null))
        assertTrue(RecordMemotest.esMejor(7, 8))
        assertFalse(RecordMemotest.esMejor(8, 8))
    }
}
