package com.example.tpjuego.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpjuego.game.LoginViewModel
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.NaranjaEstado
import com.example.tpjuego.ui.theme.TPJuegoTheme
import com.example.tpjuego.ui.theme.Turquesa

// Ingreso a la app: usuario y contraseña. Solo eso, sin mail ni datos personales.
@Composable
fun LoginScreen(
    onIngreso: (usuario: String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    // rememberSaveable: el usuario escrito sobrevive a la rotación.
    // La contraseña usa remember a propósito, para no guardarla en el estado de la Activity.
    var usuario by rememberSaveable { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    LaunchedEffect(estado.usuarioIngresado) {
        estado.usuarioIngresado?.let(onIngreso)
    }

    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(40.dp))
            Text("INMUNE GAME", style = MaterialTheme.typography.displayLarge, color = Amarillo)
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                Text(
                    if (estado.creandoCuenta) "CREAR USUARIO" else "INGRESAR",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(Modifier.height(16.dp))
                CampoTexto(
                    valor = usuario,
                    onCambio = { usuario = it },
                    etiqueta = "Usuario",
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next)
                )
                Spacer(Modifier.height(12.dp))
                CampoTexto(
                    valor = contrasena,
                    onCambio = { contrasena = it },
                    etiqueta = "Contraseña",
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    esContrasena = true
                )
                estado.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = NaranjaEstado)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario(
            texto = if (estado.creandoCuenta) "Crear y entrar" else "Entrar",
            onClick = { viewModel.enviar(usuario, contrasena) }
        )
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = { viewModel.cambiarModo() }, modifier = Modifier.fillMaxWidth()) {
            Text(
                if (estado.creandoCuenta) "Ya tengo usuario" else "Crear un usuario nuevo",
                style = MaterialTheme.typography.labelLarge,
                color = Blanco
            )
        }
    }
}

@Composable
private fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    teclado: KeyboardOptions,
    esContrasena: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyMedium) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = teclado,
        visualTransformation = if (esContrasena) PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = AzulNoche,
            unfocusedTextColor = AzulNoche,
            focusedBorderColor = Turquesa,
            focusedLabelColor = Turquesa,
            cursorColor = Turquesa
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showSystemUi = true)
@Composable
private fun LoginPreview() {
    TPJuegoTheme { LoginScreen(onIngreso = {}) }
}
