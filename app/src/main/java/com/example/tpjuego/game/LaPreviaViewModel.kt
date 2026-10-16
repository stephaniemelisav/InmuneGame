package com.example.tpjuego.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Igual que en ¿Mito o Realidad?: el ViewModel sobrevive a la rotación de pantalla,
// así la historia no vuelve a empezar al girar el celular.
class LaPreviaViewModel : ViewModel() {

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
        _historia.update { it.seguir() }
    }

    /** "Jugar otro camino": vuelve a la primera escena, sin pasar por la portada. */
    fun jugarDeNuevo() {
        _historia.value = Historia()
    }
}
