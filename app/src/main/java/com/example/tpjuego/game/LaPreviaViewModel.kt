package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.Estadisticas
import com.example.tpjuego.data.Juego
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Igual que en ¿Mito o Realidad?: el ViewModel sobrevive a la rotación de pantalla,
// así la historia no vuelve a empezar al girar el celular.
// Es AndroidViewModel para poder guardar la partida en el resumen del usuario.
class LaPreviaViewModel(application: Application) : AndroidViewModel(application) {

    private val _historia = MutableStateFlow(Historia())
    val historia: StateFlow<Historia> = _historia.asStateFlow()

    /** false mientras se muestra la portada. */
    private val _empezo = MutableStateFlow(false)
    val empezo: StateFlow<Boolean> = _empezo.asStateFlow()

    fun comenzar() {
        _empezo.value = true
    }

    fun elegir(indice: Int) {
        _historia.update { it.elegir(indice) }
    }

    fun seguir() {
        val antes = _historia.value
        val despues = antes.seguir()
        _historia.value = despues
        // Al terminar la historia, se suma una partida al resumen (resultado = decisiones tomadas).
        if (!antes.terminada && despues.terminada) {
            Estadisticas.registrarPartida(getApplication(), Juego.LA_PREVIA, despues.recorrido.size)
        }
    }

    /** "Jugar otro camino": vuelve a la primera escena, sin pasar por la portada. */
    fun jugarDeNuevo() {
        _historia.value = Historia()
    }
}
