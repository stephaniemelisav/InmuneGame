package com.example.tpjuego.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos local de la app (archivo SQLite "inmunegame.db" dentro del celular).
 * Se crea una sola instancia para toda la app (patrón singleton).
 */
@Database(entities = [PartidaEntity::class], version = 1, exportSchema = false)
abstract class InmuneDatabase : RoomDatabase() {

    abstract fun partidaDao(): PartidaDao

    companion object {
        @Volatile
        private var instancia: InmuneDatabase? = null

        fun get(context: Context): InmuneDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    InmuneDatabase::class.java,
                    "inmunegame.db"
                ).build().also { instancia = it }
            }
    }
}
