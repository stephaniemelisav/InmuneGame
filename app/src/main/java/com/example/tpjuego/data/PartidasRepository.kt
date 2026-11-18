package com.example.tpjuego.data

import android.content.Context
import com.example.tpjuego.data.local.InmuneDatabase
import com.example.tpjuego.data.local.PartidaEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Repositorio de partidas (capa Model de MVVM).
 *
 * Los ViewModel no saben si los datos vienen de Room o de Firebase: le piden todo a esta clase.
 *  - Room (SQLite, en el celular): guarda cada partida y alimenta "Mi resumen", el historial y el récord.
 *  - Firebase (Cloud Firestore, en la nube): recibe una copia de cada partida para el ranking compartido.
 *
 * Primero se guarda en Room; después se intenta subir a Firebase. Si no hay conexión, la partida
 * queda marcada como pendiente ([PartidaEntity.enLaNube] = false) y se vuelve a intentar más tarde
 * con [sincronizarPendientes].
 */
class PartidasRepository(context: Context) {

    private val app = context.applicationContext
    private val dao = InmuneDatabase.get(app).partidaDao()

    /** Guarda una partida terminada en Room y la sube al ranking de Firebase. */
    suspend fun guardar(usuario: String, juego: Juego, resultado: Int, total: Int? = null) {
        val partida = PartidaEntity(
            usuario = usuario,
            juego = juego.name,
            resultado = resultado,
            total = total,
            fechaMillis = System.currentTimeMillis()
        )
        val id = dao.insertar(partida)
        subir(partida.copy(id = id))
    }

    /** Reintenta subir a Firebase las partidas que quedaron pendientes (por ejemplo, por falta de conexión). */
    suspend fun sincronizarPendientes() {
        dao.pendientesDeSubir().forEach { subir(it) }
    }

    private fun subir(partida: PartidaEntity) {
        val juego = Juego.desdeNombre(partida.juego) ?: return
        PartidasRemotas.guardar(app, partida.usuario, juego, partida.resultado, partida.fechaMillis) { subio ->
            if (subio) escrituras.launch { dao.marcarEnLaNube(partida.id) }
        }
    }

    /** "Mi resumen": partidas, mejor resultado y última vez de cada juego. Se actualiza solo. */
    fun resumenes(usuario: String): Flow<List<ResumenJuego>> =
        dao.partidasDe(usuario).map { Resumen.armar(it) }

    /** Últimas partidas de ¿Mito o Realidad? del usuario, para la pantalla final. */
    fun historialMito(usuario: String, cantidad: Int = 5): Flow<List<ResultadoGuardado>> =
        dao.ultimas(usuario, Juego.MITO_O_REALIDAD.name, cantidad).map { lista ->
            lista.map { ResultadoGuardado(it.fechaMillis, it.resultado, it.total ?: 0) }
        }

    /** Récord del Memotest del usuario (menos intentos), o null si nunca lo terminó. */
    suspend fun recordMemotest(usuario: String): Int? =
        dao.menorResultado(usuario, Juego.MEMOTEST.name)

    /** Borra el historial local del usuario. Las partidas ya subidas al ranking quedan en la nube. */
    suspend fun borrarHistorial(usuario: String): Int = dao.borrarDe(usuario)

    companion object {
        /** Para terminar escrituras aunque la pantalla que las pidió ya se haya cerrado. */
        private val escrituras = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    }
}
