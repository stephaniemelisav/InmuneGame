package com.example.tpjuego.data

/**
 * Una afirmación del juego ¿Mito o Realidad?
 *
 * @param texto lo que circula entre adolescentes.
 * @param esRealidad true si es verdadera (realidad), false si es un mito.
 * @param explicacion texto breve que se muestra después de responder, acierte o no.
 * @param fuente de dónde sale la explicación (investigación de la Etapa 1).
 */
data class Afirmacion(
    val texto: String,
    val esRealidad: Boolean,
    val explicacion: String,
    val fuente: String
)
