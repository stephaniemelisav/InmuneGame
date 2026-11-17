package com.example.tpjuego.data

/** Una pareja del Memotest: la sustancia (lo que dice la carta) y la nota que se lee al encontrarla. */
data class Tema(val nombre: String, val nota: String)

/** Datos del Memotest (juego de Enzo). */
object MemotestTemas {
    val temas = listOf(
        Tema("Alcohol", "El alcohol deprime el sistema nervioso central y altera los reflejos y el juicio."),
        Tema("Tabaco", "La nicotina genera alta dependencia."),
        Tema("Marihuana", "La marihuana afecta la memoria y la atención."),
        Tema("Energizante + alcohol", "Mezclar energizante con alcohol disimula la intoxicación y es muy riesgoso."),
        Tema("Cocaína", "La cocaína aumenta el riesgo de problemas cardíacos."),
        Tema("Pedir ayuda", "Hablar con una persona de confianza es el primer paso para cuidarte.")
    )
}
