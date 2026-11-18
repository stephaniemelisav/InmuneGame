package com.example.tpjuego.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO de la tabla "partidas": todas las consultas y modificaciones de la base local.
 * Las consultas que devuelven Flow se actualizan solas cuando cambia la tabla,
 * así "Mi resumen" y el historial se refrescan sin recargar la pantalla.
 */
@Dao
interface PartidaDao {

    /** Alta: guarda una partida terminada. Devuelve el id generado. */
    @Insert
    suspend fun insertar(partida: PartidaEntity): Long

    /** Todas las partidas de un usuario, de la más nueva a la más vieja. */
    @Query("SELECT * FROM partidas WHERE usuario = :usuario ORDER BY fechaMillis DESC")
    fun partidasDe(usuario: String): Flow<List<PartidaEntity>>

    /** Las últimas [cantidad] partidas de un juego para un usuario. */
    @Query(
        "SELECT * FROM partidas WHERE usuario = :usuario AND juego = :juego " +
            "ORDER BY fechaMillis DESC LIMIT :cantidad"
    )
    fun ultimas(usuario: String, juego: String, cantidad: Int): Flow<List<PartidaEntity>>

    /** Menor resultado de un juego (récord del Memotest: menos intentos). null si no hay partidas. */
    @Query("SELECT MIN(resultado) FROM partidas WHERE usuario = :usuario AND juego = :juego")
    suspend fun menorResultado(usuario: String, juego: String): Int?

    /** Partidas que todavía no se pudieron subir a Firebase (por ejemplo, sin conexión). */
    @Query("SELECT * FROM partidas WHERE enLaNube = 0")
    suspend fun pendientesDeSubir(): List<PartidaEntity>

    /** Modificación: marca una partida como ya subida al ranking. */
    @Query("UPDATE partidas SET enLaNube = 1 WHERE id = :id")
    suspend fun marcarEnLaNube(id: Long)

    /** Baja: borra el historial local de un usuario. Devuelve cuántas filas borró. */
    @Query("DELETE FROM partidas WHERE usuario = :usuario")
    suspend fun borrarDe(usuario: String): Int
}
