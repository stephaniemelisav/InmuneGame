package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.HistorialPartidas
import com.example.tpjuego.data.ResultadoGuardado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// El ViewModel sobrevive a la rotación de pantalla: la partida no se pierde al girar el celular.
// Es AndroidViewModel porque necesita el Context para guardar el historial.
class MitoORealidadViewModel(application: Application) : AndroidViewModel(application) {

    private val historial = HistorialPartidas(application)

    private val _partida = MutableStateFlow(nuevaPartida())
    val partida: StateFlow<Partida> = _partida.asStateFlow()

    /** Partidas terminadas, de la más reciente a la más vieja. */
    private val _historial = MutableStateFlow(historial.leer())
    val partidasAnteriores: StateFlow<List<ResultadoGuardado>> = _historial.asStateFlow()

    fun responder(eligioRealidad: Boolean) {
        _partida.update { it.responder(eligioRealidad) }
    }

    fun siguiente() {
        val antes = _partida.value
        val despues = antes.siguiente()
        _partida.value = despues
        // Al terminar, se guarda una sola vez en el historial.
        if (!antes.terminada && despues.terminada) {
            _historial.value = historial.guardar(
                ResultadoGuardado(
                    fechaMillis = System.currentTimeMillis(),
                    aciertos = despues.aciertos,
                    total = despues.total
                )
            )
        }
    }

    fun jugarDeNuevo() {
        _partida.value = nuevaPartida()
    }

    private fun nuevaPartida() = Partida(AfirmacionesRepositorio.paraPartida())
}
