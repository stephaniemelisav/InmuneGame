package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tpjuego.data.EstadoNube
import com.example.tpjuego.data.PartidasRemotas
import com.example.tpjuego.data.PartidasRepository
import com.example.tpjuego.data.ResumenJuego
import com.example.tpjuego.data.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel de "Mi resumen".
 *  - MIS JUEGOS: se lee de Room con un Flow, así se actualiza solo.
 *  - EN LA NUBE: las últimas partidas del usuario en Firestore.
 */
class ResumenViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = PartidasRepository(application)
    val usuario: String = Sesion.usuarioOInvitado(application)

    val resumenes: StateFlow<List<ResumenJuego>> = repositorio.resumenes(usuario)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _nube = MutableStateFlow<EstadoNube>(EstadoNube.Cargando)
    val nube: StateFlow<EstadoNube> = _nube.asStateFlow()

    init {
        viewModelScope.launch {
            // Si alguna partida no se pudo subir antes (sin conexión), se reintenta ahora.
            repositorio.sincronizarPendientes()
        }
        cargarNube()
    }

    fun cargarNube() {
        _nube.value = EstadoNube.Cargando
        PartidasRemotas.ultimas(getApplication(), usuario) { _nube.value = it }
    }

    /** Borra el historial del celular (tabla de Room). Lo que ya está en el ranking no se toca. */
    fun borrarHistorial() {
        viewModelScope.launch { repositorio.borrarHistorial(usuario) }
    }
}
