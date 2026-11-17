package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.Estadisticas
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.Sesion
import com.example.tpjuego.data.HistorialPartidas
import com.example.tpjuego.data.ResultadoGuardado
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
// Es AndroidViewModel porque necesita el Context para guardar el historial.
class MitoORealidadViewModel(application: Application) : AndroidViewModel(application) {

    private val historial = HistorialPartidas(application, Sesion.usuarioOInvitado(application))

    private val _partida = MutableStateFlow(nuevaPartida())
    val partida: StateFlow<Partida> = _partida.asStateFlow()

    /** Partidas terminadas, de la más reciente a la más vieja. */
    private val _historial = MutableStateFlow(historial.leer())
    val partidasAnteriores: StateFlow<List<ResultadoGuardado>> = _historial.asStateFlow()

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
        // Al terminar, se guarda una sola vez en el historial.
        if (!antes.terminada && despues.terminada) {
            _historial.value = historial.guardar(
                ResultadoGuardado(
                    fechaMillis = System.currentTimeMillis(),
                    aciertos = despues.aciertos,
                    total = despues.total
                )
            )
            // Suma la partida al resumen del usuario.
            Estadisticas.registrarPartida(getApplication(), Juego.MITO_O_REALIDAD, despues.aciertos)
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
