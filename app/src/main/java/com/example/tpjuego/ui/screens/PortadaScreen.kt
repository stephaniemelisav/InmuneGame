package com.example.tpjuego.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.Etiqueta
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Portada de ¿Mito o Realidad?
@Composable
fun PortadaScreen(onComenzar: () -> Unit, onVolver: () -> Unit) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))
            Etiqueta("JUEGO DE PREGUNTAS", fondo = Amarillo, colorTexto = AzulNoche)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "¿MITO O REALIDAD?",
                style = MaterialTheme.typography.displayLarge,
                color = Blanco
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "¿Cuánto sabés realmente?",
                style = MaterialTheme.typography.titleMedium,
                color = Blanco
            )
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                Text(
                    text = "${AfirmacionesRepositorio.PREGUNTAS_POR_PARTIDA} preguntas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sin reloj y sin vidas: tomate tu tiempo para pensar.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Comencemos", onClick = onComenzar)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Volver al menú", onClick = onVolver)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PortadaScreenPreview() {
    TPJuegoTheme {
        PortadaScreen(onComenzar = {}, onVolver = {})
    }
}
