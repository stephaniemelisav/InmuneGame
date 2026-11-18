package com.example.tpjuego.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabla "partidas" de la base local (Room sobre SQLite).
 * Cada fila es una partida terminada de cualquiera de los tres juegos.
 *
 * - [juego]: nombre del enum Juego (MITO_O_REALIDAD, MEMOTEST o LA_PREVIA).
 * - [resultado]: aciertos (Mito), intentos (Memotest) o decisiones (La Previa).
 * - [total]: cantidad de preguntas en Mito (para mostrar "4/5"); null en los otros juegos.
 * - [enLaNube]: true cuando la partida ya se subió al ranking de Firebase.
 */
@Entity(
    tableName = "partidas",
    indices = [Index("usuario"), Index("juego")]
)
data class PartidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuario: String,
    val juego: String,
    val resultado: Int,
    val total: Int? = null,
    val fechaMillis: Long,
    val enLaNube: Boolean = false
)
