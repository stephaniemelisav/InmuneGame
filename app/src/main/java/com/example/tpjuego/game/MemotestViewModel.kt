package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.PartidasRepository
import com.example.tpjuego.data.Sesion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Avisos para la pantalla (sonido y vibración). Son eventos: no se repiten al rotar. */
sealed class EventoMemotest {
    data object ParEncontrado : EventoMemotest()
    data object JuegoCompletado : EventoMemotest()
}

/**
 * ViewModel del Memotest. Sobrevive a la rotación: la partida sigue donde estaba.
 * Al terminar guarda la partida en Room (resumen y récord) y la sube al ranking de Firebase.
 */
class MemotestViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PartidasRepository(application)
    private val usuario = Sesion.usuarioOInvitado(application)

    private val _juego = MutableStateFlow(Memotest())
    val juego: StateFlow<Memotest> = _juego.asStateFlow()

    private val _mejorMarca = MutableStateFlow<Int?>(null)
    val mejorMarca: StateFlow<Int?> = _mejorMarca.asStateFlow()

    private val _nuevoRecord = MutableStateFlow(false)
    val nuevoRecord: StateFlow<Boolean> = _nuevoRecord.asStateFlow()

    private val _eventos = MutableSharedFlow<EventoMemotest>(extraBufferCapacity = 4)
    val eventos: SharedFlow<EventoMemotest> = _eventos.asSharedFlow()

    private var taparJob: Job? = null

    init {
        // El récord se lee de Room (la consulta corre en segundo plano).
        viewModelScope.launch { _mejorMarca.value = repositorio.recordMemotest(usuario) }
    }

    fun tocar(indice: Int) {
        val despues = _juego.value.tocar(indice)
        _juego.value = despues
        when (despues.ultimoToque) {
            ResultadoToque.PAR_ENCONTRADO -> _eventos.tryEmit(EventoMemotest.ParEncontrado)
            ResultadoToque.NO_COINCIDE -> taparJob = viewModelScope.launch {
                delay(TIEMPO_PARA_LEER_MS)
                _juego.value = _juego.value.tapar()
            }
            else -> Unit
        }
    }

    fun abrirNota(id: Int) {
        _juego.value = _juego.value.abrirNota(id)
    }

    fun cerrarNota() {
        val antes = _juego.value
        val despues = antes.cerrarNota()
        _juego.value = despues
        if (!antes.mostrarFinal && despues.mostrarFinal) terminar(despues.intentos)
    }

    /** Se ejecuta una sola vez por partida. */
    private fun terminar(intentos: Int) {
        _eventos.tryEmit(EventoMemotest.JuegoCompletado)
        viewModelScope.launch {
            val anterior = repositorio.recordMemotest(usuario)
            _nuevoRecord.value = Juego.MEMOTEST.esMejor(intentos, anterior)
            // Guarda en Room (resumen y récord) y sube al ranking de Firebase.
            repositorio.guardar(usuario, Juego.MEMOTEST, intentos)
            _mejorMarca.value = repositorio.recordMemotest(usuario)
        }
    }

    fun reiniciar() {
        taparJob?.cancel()
        _nuevoRecord.value = false
        _juego.value = Memotest()
    }

    companion object {
        const val TIEMPO_PARA_LEER_MS = 1000L
    }
}
