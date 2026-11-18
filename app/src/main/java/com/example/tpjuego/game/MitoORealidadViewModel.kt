package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.PartidasRepository
import com.example.tpjuego.data.ResultadoGuardado
import com.example.tpjuego.data.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Avisos del reloj para la pantalla (sonido y vibración). No se repiten al rotar. */
sealed class EventoReloj {
    data class Tic(val segundosRestantes: Int) : EventoReloj()
    data object SeAcaboElTiempo : EventoReloj()
}

// El ViewModel sobrevive a la rotación de pantalla: la partida no se pierde al girar el celular.
// Es AndroidViewModel porque necesita el Context para abrir la base de datos (Room).
class MitoORealidadViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PartidasRepository(application)
    private val usuario = Sesion.usuarioOInvitado(application)

    private val _partida = MutableStateFlow(nuevaPartida())
    val partida: StateFlow<Partida> = _partida.asStateFlow()

    /** Últimas partidas del usuario, leídas de Room (de la más reciente a la más vieja). Se actualiza sola. */
    val partidasAnteriores: StateFlow<List<ResultadoGuardado>> = repositorio.historialMito(usuario)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ---------- Reloj: 10 segundos por pregunta ----------

    private val _segundos = MutableStateFlow(SEGUNDOS_POR_PREGUNTA)
    val segundosRestantes: StateFlow<Int> = _segundos.asStateFlow()

    private val _eventosReloj = MutableSharedFlow<EventoReloj>(extraBufferCapacity = 4)
    val eventosReloj: SharedFlow<EventoReloj> = _eventosReloj.asSharedFlow()

    private var reloj: Job? = null

    /**
     * Arranca (o retoma) la cuenta regresiva de la pregunta actual.
     * La pantalla lo llama en onResume y lo frena en onPause, así el reloj
     * no corre con la app en segundo plano. Al rotar sigue donde estaba.
     */
    fun arrancarReloj() {
        val p = _partida.value
        if (reloj?.isActive == true || p.terminada || p.respondida) return
        reloj = viewModelScope.launch {
            while (_segundos.value > 0) {
                delay(1000)
                _segundos.value -= 1
                if (_segundos.value > 0) _eventosReloj.tryEmit(EventoReloj.Tic(_segundos.value))
            }
            _partida.update { it.sinTiempo() }
            _eventosReloj.tryEmit(EventoReloj.SeAcaboElTiempo)
        }
    }

    fun pausarReloj() {
        reloj?.cancel()
        reloj = null
    }

    private fun reiniciarReloj() {
        pausarReloj()
        _segundos.value = SEGUNDOS_POR_PREGUNTA
        arrancarReloj()
    }

    fun responder(eligioRealidad: Boolean) {
        pausarReloj()
        _partida.update { it.responder(eligioRealidad) }
    }

    fun siguiente() {
        val antes = _partida.value
        val despues = antes.siguiente()
        _partida.value = despues
        if (!despues.terminada) reiniciarReloj()
        // Al terminar, se guarda una sola vez: en Room (historial y resumen) y en el ranking de Firebase.
        if (!antes.terminada && despues.terminada) {
            viewModelScope.launch {
                repositorio.guardar(usuario, Juego.MITO_O_REALIDAD, despues.aciertos, despues.total)
            }
        }
    }

    fun jugarDeNuevo() {
        _partida.value = nuevaPartida()
        reiniciarReloj()
    }

    private fun nuevaPartida() = Partida(AfirmacionesRepositorio.paraPartida())

    companion object {
        const val SEGUNDOS_POR_PREGUNTA = 10
    }
}
