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
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

private val pasos = listOf(
    "Leé la afirmación.",
    "Elegí si es Verdadero (realidad) o Falso (mito).",
    "Leé la explicación antes de seguir: equivocarse también enseña."
)

// Pantalla "Cómo se juega": tres pasos cortos antes de la primera pregunta.
@Composable
fun ComoSeJuegaScreen(onJugar: () -> Unit) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))
            Text(
                text = "CÓMO SE JUEGA",
                style = MaterialTheme.typography.headlineLarge,
                color = Blanco
            )
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                pasos.forEachIndexed { i, paso ->
                    if (i > 0) Spacer(Modifier.height(20.dp))
                    Paso(numero = i + 1, texto = paso)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("¡A jugar!", onClick = onJugar)
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
private fun ComoSeJuegaScreenPreview() {
    TPJuegoTheme {
        ComoSeJuegaScreen(onJugar = {})
    }
}
