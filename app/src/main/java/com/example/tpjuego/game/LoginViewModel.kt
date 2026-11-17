package com.example.tpjuego.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.tpjuego.data.ResultadoAcceso
import com.example.tpjuego.data.Sesion
import com.example.tpjuego.data.Usuarios
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Estado de la pantalla de ingreso. */
data class EstadoLogin(
    val creandoCuenta: Boolean = false,
    val error: String? = null,
    val usuarioIngresado: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarios = Usuarios(application)

    private val _estado = MutableStateFlow(EstadoLogin())
    val estado: StateFlow<EstadoLogin> = _estado.asStateFlow()

    /** Cambia entre "Ingresar" y "Crear usuario". */
    fun cambiarModo() {
        _estado.value = EstadoLogin(creandoCuenta = !_estado.value.creandoCuenta)
    }

    fun enviar(nombre: String, contrasena: String) {
        val resultado = if (_estado.value.creandoCuenta) {
            usuarios.registrar(nombre, contrasena)
        } else {
            usuarios.iniciarSesion(nombre, contrasena)
        }
        _estado.value = when (resultado) {
            is ResultadoAcceso.Ok -> {
                Sesion.iniciar(getApplication(), resultado.usuario)
                _estado.value.copy(error = null, usuarioIngresado = resultado.usuario)
            }
            is ResultadoAcceso.Error -> _estado.value.copy(error = resultado.mensaje)
        }
    }
}
