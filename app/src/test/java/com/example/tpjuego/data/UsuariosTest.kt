package com.example.tpjuego.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UsuariosTest {

    @Test
    fun normalizarIgnoraMayusculasYEspacios() {
        assertEquals("vale", Usuarios.normalizar("  Vale "))
    }

    @Test
    fun validarAceptaDatosCorrectos() {
        assertNull(Usuarios.validar("vale_22", "1234"))
    }

    @Test
    fun validarRechazaDatosInvalidos() {
        assertNotNull(Usuarios.validar("va", "1234"))          // usuario corto
        assertNotNull(Usuarios.validar("va le", "1234"))       // espacio
        assertNotNull(Usuarios.validar("vale", "12"))          // contraseña corta
        assertNotNull(Usuarios.validar("a".repeat(21), "1234")) // usuario largo
    }

    @Test
    fun laContrasenaNoSeGuardaEnTextoPlano() {
        val guardada = Credencial.crear("secreto1")
        assertFalse(guardada.contains("secreto1"))
        assertTrue(Credencial.verificar("secreto1", guardada))
        assertFalse(Credencial.verificar("otra", guardada))
    }

    @Test
    fun cadaHashUsaUnaSalDistinta() {
        assertNotEquals(Credencial.crear("1234"), Credencial.crear("1234"))
    }

    @Test
    fun unValorGuardadoRotoNoDejaEntrar() {
        assertFalse(Credencial.verificar("1234", "basura"))
    }
}
