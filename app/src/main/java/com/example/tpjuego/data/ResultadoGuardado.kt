package com.example.tpjuego.data

/** Resultado de una partida terminada, para el historial. */
data class ResultadoGuardado(
    val fechaMillis: Long,
    val aciertos: Int,
    val total: Int
) {
    val errores: Int get() = total - aciertos
}

/**
 * Convierte el historial a texto y viceversa, para guardarlo en SharedPreferences.
 * Formato: una partida por línea, "fecha;aciertos;total".
 * Está separado de Android para poder probarlo con tests unitarios.
 */
object HistorialCodec {

    fun codificar(resultados: List<ResultadoGuardado>): String =
        resultados.joinToString("\n") { "${it.fechaMillis};${it.aciertos};${it.total}" }

    fun decodificar(texto: String): List<ResultadoGuardado> =
        texto.lines().mapNotNull { linea ->
            val partes = linea.split(";")
            if (partes.size != 3) return@mapNotNull null
            val fecha = partes[0].toLongOrNull() ?: return@mapNotNull null
            val aciertos = partes[1].toIntOrNull() ?: return@mapNotNull null
            val total = partes[2].toIntOrNull() ?: return@mapNotNull null
            ResultadoGuardado(fecha, aciertos, total)
        }
}
