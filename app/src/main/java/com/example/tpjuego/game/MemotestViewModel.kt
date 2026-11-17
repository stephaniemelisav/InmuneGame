package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tpjuego.data.Estadisticas
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.RecordMemotest
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
 * Al terminar guarda la partida en el resumen del usuario, en la nube y su récord.
 */
class MemotestViewModel(application: Application) : AndroidViewModel(application) {

    private val record = RecordMemotest(application, Sesion.usuarioOInvitado(application))

    private val _juego = MutableStateFlow(Memotest())
    val juego: StateFlow<Memotest> = _juego.asStateFlow()

    private val _mejorMarca = MutableStateFlow(record.leer())
    val mejorMarca: StateFlow<Int?> = _mejorMarca.asStateFlow()

    private val _nuevoRecord = MutableStateFlow(false)
    val nuevoRecord: StateFlow<Boolean> = _nuevoRecord.asStateFlow()

    private val _eventos = MutableSharedFlow<EventoMemotest>(extraBufferCapacity = 4)
    val eventos: SharedFlow<EventoMemotest> = _eventos.asSharedFlow()

    private var taparJob: Job? = null

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
        Estadisticas.registrarPartida(getApplication(), Juego.MEMOTEST, intentos)
        _nuevoRecord.value = record.guardarSiEsMejor(intentos)
        _mejorMarca.value = record.leer()
        _eventos.tryEmit(EventoMemotest.JuegoCompletado)
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
