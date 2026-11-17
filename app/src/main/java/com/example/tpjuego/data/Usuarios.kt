package com.example.tpjuego.data

import android.content.Context
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** Resultado de crear un usuario o iniciar sesión. */
sealed class ResultadoAcceso {
    data class Ok(val usuario: String) : ResultadoAcceso()
    data class Error(val mensaje: String) : ResultadoAcceso()
}

/**
 * Usuarios de la app, guardados solo en este celular (SharedPreferences).
 *
 * La contraseña NUNCA se guarda en texto plano: se guarda un hash PBKDF2 con una
 * "sal" aleatoria por usuario. Al iniciar sesión se vuelve a calcular el hash y se compara.
 * Solo se pide usuario y contraseña: no se piden mail ni datos personales.
 */
class Usuarios(context: Context) {

    private val prefs = context.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)

    fun registrar(nombre: String, contrasena: String): ResultadoAcceso {
        val usuario = normalizar(nombre)
        validar(usuario, contrasena)?.let { return ResultadoAcceso.Error(it) }
        if (prefs.contains(clave(usuario))) {
            return ResultadoAcceso.Error("Ese usuario ya existe. Probá con otro nombre o ingresá.")
        }
        prefs.edit().putString(clave(usuario), Credencial.crear(contrasena)).apply()
        return ResultadoAcceso.Ok(usuario)
    }

    fun iniciarSesion(nombre: String, contrasena: String): ResultadoAcceso {
        val usuario = normalizar(nombre)
        val guardada = prefs.getString(clave(usuario), null)
        // Mismo mensaje si no existe el usuario o si la contraseña está mal:
        // así no se puede averiguar qué usuarios existen.
        if (guardada == null || !Credencial.verificar(contrasena, guardada)) {
            return ResultadoAcceso.Error("Usuario o contraseña incorrectos.")
        }
        return ResultadoAcceso.Ok(usuario)
    }

    private fun clave(usuario: String) = "u_$usuario"

    companion object {
        private const val ARCHIVO = "usuarios"
        const val MIN_NOMBRE = 3
        const val MAX_NOMBRE = 20
        const val MIN_CONTRASENA = 4

        /** El nombre no distingue mayúsculas ni espacios de más: "Vale " = "vale". */
        fun normalizar(nombre: String): String = nombre.trim().lowercase()

        /** @return el mensaje de error, o null si los datos son válidos. */
        fun validar(usuario: String, contrasena: String): String? = when {
            usuario.length < MIN_NOMBRE -> "El usuario tiene que tener al menos $MIN_NOMBRE letras."
            usuario.length > MAX_NOMBRE -> "El usuario puede tener hasta $MAX_NOMBRE letras."
            !usuario.all { it.isLetterOrDigit() || it == '_' || it == '.' } ->
                "Usá solo letras, números, punto o guion bajo."
            contrasena.length < MIN_CONTRASENA -> "La contraseña tiene que tener al menos $MIN_CONTRASENA caracteres."
            else -> null
        }
    }
}

/**
 * Hash de contraseñas con PBKDF2 y sal aleatoria. Formato guardado: "sal:hash" en hexadecimal
 * (se usa hexadecimal y no Base64 de Java porque ese necesita API 26 y la app soporta desde API 24).
 * Es Kotlin/Java puro, así se prueba con tests unitarios.
 */
object Credencial {
    private const val ITERACIONES = 10_000
    private const val LARGO_BITS = 256

    fun crear(contrasena: String): String {
        val sal = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return "${hex(sal)}:${hex(hash(contrasena, sal))}"
    }

    fun verificar(contrasena: String, guardada: String): Boolean {
        val partes = guardada.split(":")
        if (partes.size != 2) return false
        val sal = desdeHex(partes[0]) ?: return false
        val esperado = desdeHex(partes[1]) ?: return false
        // Comparación en tiempo constante.
        return MessageDigest.isEqual(hash(contrasena, sal), esperado)
    }

    private fun hash(contrasena: String, sal: ByteArray): ByteArray {
        val spec = PBEKeySpec(contrasena.toCharArray(), sal, ITERACIONES, LARGO_BITS)
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec).encoded
    }

    private fun hex(bytes: ByteArray) = bytes.joinToString("") { "%02x".format(it) }

    private fun desdeHex(texto: String): ByteArray? {
        if (texto.length % 2 != 0) return null
        return try {
            ByteArray(texto.length / 2) { i -> texto.substring(2 * i, 2 * i + 2).toInt(16).toByte() }
        } catch (e: NumberFormatException) {
            null
        }
    }
}
