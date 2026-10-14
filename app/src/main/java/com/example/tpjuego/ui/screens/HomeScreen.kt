package com.example.tpjuego.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.TarjetaJuego
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Menú de juegos: lo primero que se ve al abrir la app, sin registro.
// Las tarjetas van en la mitad de abajo, al alcance del pulgar.
@Composable
fun HomeScreen(
    onMitoORealidadClick: () -> Unit,
    onMemotestClick: () -> Unit,
    onVerIntro: () -> Unit
) {
    PantallaDegradado {
        Spacer(Modifier.height(32.dp))
        Text(
            text = "INMUNE GAME",
            style = MaterialTheme.typography.headlineLarge,
            color = Blanco
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Elegí un juego",
            style = MaterialTheme.typography.titleMedium,
            color = Blanco
        )
        TextButton(onClick = onVerIntro, contentPadding = PaddingValues(0.dp)) {
            Text("▶  Ver la introducción", style = MaterialTheme.typography.labelLarge, color = Blanco)
        }
        Spacer(Modifier.weight(1f))
        TarjetaJuego(
            titulo = "¿Mito o Realidad?",
            descripcion = "Decidí si lo que se dice entre amigos es verdad o un mito. 5 preguntas.",
            onClick = onMitoORealidadClick
        )
        Spacer(Modifier.height(16.dp))
        TarjetaJuego(
            titulo = "Memotest",
            descripcion = "Encontrá los pares: cada sustancia con su dato. Juego de Enzo.",
            onClick = onMemotestClick
        )
    }
}

// Con el preview mostramos en el panel split, útil para desarrollar estos componentes y visualizar la previa
@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    TPJuegoTheme {
        HomeScreen(onMitoORealidadClick = {}, onMemotestClick = {}, onVerIntro = {})
    }
}
