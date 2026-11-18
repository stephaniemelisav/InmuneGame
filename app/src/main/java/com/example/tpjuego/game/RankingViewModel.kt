package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.EstadoRanking
import com.example.tpjuego.data.Juego
import com.example.tpjuego.data.PartidasRemotas
import com.example.tpjuego.data.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** ViewModel del ranking compartido (Cloud Firestore). Guarda el juego elegido y el estado de la carga. */
class RankingViewModel(application: Application) : AndroidViewModel(application) {

    /** Los juegos con puntaje comparable. */
    val juegos = listOf(Juego.MITO_O_REALIDAD, Juego.MEMOTEST)

    val usuario: String = Sesion.usuarioOInvitado(application)

    private val _juego = MutableStateFlow(Juego.MITO_O_REALIDAD)
    val juego: StateFlow<Juego> = _juego.asStateFlow()

    private val _estado = MutableStateFlow<EstadoRanking>(EstadoRanking.Cargando)
    val estado: StateFlow<EstadoRanking> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun elegir(juego: Juego) {
        if (juego == _juego.value) return
        _juego.value = juego
        cargar()
    }

    /** También es el botón "Reintentar" cuando falla la conexión. */
    fun cargar() {
        val pedido = _juego.value
        _estado.value = EstadoRanking.Cargando
        PartidasRemotas.ranking(getApplication(), pedido) { resultado ->
            // Si mientras cargaba se eligió otro juego, se descarta esta respuesta.
            if (_juego.value == pedido) _estado.value = resultado
        }
    }
}
