package com.example.tpjuego.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.R
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.Etiqueta
import com.example.tpjuego.ui.components.MusicaDeFondo
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Los mismos tres pasos que tenía ComoSeJuegaScreen
private val pasos = listOf(
    "Leé la frase: tenés 10 segundos.",
    "Tocá Verdadero o Falso, o incliná el celu.",
    "Mirá la explicación y seguí."
)

// Portada de ¿Mito o Realidad? con las reglas de cómo se juega
@Composable
fun PortadaScreen(onComenzar: () -> Unit, onVolver: () -> Unit) {
    MusicaDeFondo(R.raw.introduccion1)

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
                    text = "10 segundos por frase.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text(text = "CÓMO SE JUEGA", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(16.dp))
                pasos.forEachIndexed { i, paso ->
                    if (i > 0) Spacer(Modifier.height(20.dp))
                    Paso(numero = i + 1, texto = paso)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Comencemos", onClick = onComenzar)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Volver al menú", onClick = onVolver)
    }
}

@Composable
private fun Paso(numero: Int, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Amarillo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero.toString(),
                style = MaterialTheme.typography.headlineMedium,
                color = AzulNoche
            )
        }
        Spacer(Modifier.width(16.dp))
        Text(text = texto, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun PortadaScreenPreview() {
    TPJuegoTheme {
        PortadaScreen(onComenzar = {}, onVolver = {})
    }
}