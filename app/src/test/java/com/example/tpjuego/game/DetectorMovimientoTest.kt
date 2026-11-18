package com.example.tpjuego.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DetectorMovimientoTest {

    private val gravedad = 9.8f

    @Test
    fun celularDerechoNoDetectaNada() {
        assertNull(DetectorMovimiento().procesar(0f, gravedad, 0f, 0))
    }

    @Test
    fun inclinarALaDerechaYALaIzquierda() {
        val d = DetectorMovimiento()
        d.procesar(0f, gravedad, 0f, 0)
        assertEquals(Movimiento.INCLINAR_DERECHA, d.procesar(-7f, 6f, 0f, 50))
        d.procesar(0f, gravedad, 0f, 100)
        assertEquals(Movimiento.INCLINAR_IZQUIERDA, d.procesar(7f, 6f, 0f, 200))
    }

    @Test
    fun hayQueVolverAlCentroAntesDeOtraInclinacion() {
        val d = DetectorMovimiento()
        d.procesar(0f, gravedad, 0f, 0)
        assertEquals(Movimiento.INCLINAR_DERECHA, d.procesar(-7f, 6f, 0f, 50))
        // Sigue inclinado: no responde dos veces.
        assertNull(d.procesar(-8f, 5f, 0f, 100))
    }

    @Test
    fun siArrancaInclinadoNoRespondeHastaEnderezarse() {
        val d = DetectorMovimiento()
        assertNull(d.procesar(-7f, 6f, 0f, 0))
        d.procesar(0f, gravedad, 0f, 100)
        assertEquals(Movimiento.INCLINAR_DERECHA, d.procesar(-7f, 6f, 0f, 200))
    }

    @Test
    fun sacudirTieneUnaEsperaEntreGestos() {
        val d = DetectorMovimiento()
        assertEquals(Movimiento.SACUDIR, d.procesar(20f, 20f, 10f, 0))
        // Medio segundo después no vuelve a contar.
        assertEquals(null, d.procesar(20f, 20f, 10f, 500).takeIf { it == Movimiento.SACUDIR })
        assertEquals(Movimiento.SACUDIR, d.procesar(20f, 20f, 10f, 1500))
    }
}
