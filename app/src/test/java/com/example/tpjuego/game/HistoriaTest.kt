package com.example.tpjuego.game

import com.example.tpjuego.data.LaPreviaHistoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoriaTest {

    @Test
    fun empiezaEnLaPrimeraEscenaSinElegir() {
        val h = Historia()
        assertEquals(LaPreviaHistoria.INICIO, h.actualId)
        assertNull(h.elegida)
        assertFalse(h.terminada)
    }

    @Test
    fun elegirMuestraLaConsecuenciaSinAvanzar() {
        val h = Historia().elegir(0)
        assertNotNull(h.elegida)
        assertEquals(LaPreviaHistoria.INICIO, h.actualId)
        assertEquals(1, h.recorrido.size)
    }

    @Test
    fun soloCuentaLaPrimeraEleccion() {
        val h = Historia().elegir(0).elegir(1)
        assertEquals(1, h.recorrido.size)
        assertEquals(h.actual.opciones[0], h.elegida)
    }

    @Test
    fun noAvanzaSinElegir() {
        val h = Historia()
        assertEquals(h, h.seguir())
    }

    @Test
    fun opcionInexistenteSeIgnora() {
        val h = Historia()
        assertEquals(h, h.elegir(99))
    }

    @Test
    fun cadaDecisionLlevaASuCamino() {
        val dijoQueNo = Historia().elegir(0).seguir().elegir(0).seguir()
        assertEquals("insisten", dijoQueNo.actualId)

        val probo = Historia().elegir(0).seguir().elegir(2).seguir()
        assertEquals("malestar", probo.actualId)
    }

    @Test
    fun todasLasOpcionesLlevanAEscenasQueExisten() {
        LaPreviaHistoria.escenas.values.forEach { escena ->
            assertTrue("La escena ${escena.id} no tiene opciones", escena.opciones.isNotEmpty())
            escena.opciones.forEach { opcion ->
                val siguiente = opcion.siguiente
                if (siguiente != null) {
                    assertTrue(
                        "La opción '${opcion.texto}' lleva a '$siguiente', que no existe",
                        siguiente in LaPreviaHistoria.escenas
                    )
                }
            }
        }
    }

    @Test
    fun todosLosCaminosTerminanYPasanPorLaMismaCantidadDeEscenas() {
        // Recorre todos los caminos posibles desde el inicio.
        fun recorrer(h: Historia, pasos: Int): Int {
            if (h.terminada) {
                assertEquals(LaPreviaHistoria.TOTAL_ESCENAS, pasos)
                assertEquals(pasos, h.recorrido.size)
                return 1
            }
            assertEquals(pasos + 1, h.actual.numero)
            return h.actual.opciones.indices.sumOf { i -> recorrer(h.elegir(i).seguir(), pasos + 1) }
        }
        val caminos = recorrer(Historia(), 0)
        assertTrue("Debería haber varios caminos", caminos > 1)
    }

    @Test
    fun alTerminarSiempreEstaLaLinea141YSinRepetidos() {
        var h = Historia()
        while (!h.terminada) h = h.elegir(0).seguir()
        assertTrue(LaPreviaHistoria.recursoFinal in h.recursos)
        assertEquals(h.recursos.size, h.recursos.distinct().size)
    }

    @Test
    fun terminadaNoCambiaMas() {
        var h = Historia()
        while (!h.terminada) h = h.elegir(0).seguir()
        assertEquals(h, h.elegir(0))
        assertEquals(h, h.seguir())
    }
}
