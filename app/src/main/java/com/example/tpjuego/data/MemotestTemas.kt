package com.example.tpjuego.data

/** Una pareja del Memotest: la sustancia (lo que dice la carta) y la nota que se lee al encontrarla. */
data class Tema(val nombre: String, val nota: String)

/** Datos del Memotest (juego de Enzo). */
object MemotestTemas {
    val temas = listOf(
        Tema("Heroína", "Es un opioide que afecta el sistema nervioso. Puede causar dependencia, pérdida de conciencia y sobredosis mortal por detener la respiración."),
        Tema("Metanfetamina", "Es un potente estimulante. Puede provocar insomnio, paranoia, problemas cardíacos y daños en el cerebro."),
        Tema("LSD", "Es una droga alucinógena que altera la percepción y los sentidos. Puede provocar pánico, confusión y episodios de ansiedad intensa."),
        Tema("Éxtasis", "Altera el estado de ánimo y la percepción. Puede causar deshidratación, aumento peligroso de la temperatura corporal y problemas cardíacos."),
        Tema("Cocaína", "Es un estimulante que acelera el sistema nervioso. Puede provocar ansiedad, taquicardia, adicción e infartos."),
        Tema("Crack", "Es una forma de cocaína de efecto muy rápido. Puede generar una fuerte dependencia, ansiedad, daños pulmonares y problemas cardiovasculares.")
    )
}
