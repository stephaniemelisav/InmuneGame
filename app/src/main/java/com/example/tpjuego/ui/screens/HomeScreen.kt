package com.example.tpjuego.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.TarjetaJuego
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Menú de juegos. Las tarjetas van en la mitad de abajo, al alcance del pulgar.
@Composable
fun HomeScreen(
    usuario: String,
    onMitoORealidadClick: () -> Unit,
    onMemotestClick: () -> Unit,
    onLaPreviaClick: () -> Unit,
    onVerIntro: () -> Unit,
    onVerResumen: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    PantallaDegradado {
        Spacer(Modifier.height(16.dp))
        Text("HOLA, ${usuario.uppercase()}", style = MaterialTheme.typography.titleSmall, color = Amarillo)
        Text("ELEGÍ UN JUEGO", style = MaterialTheme.typography.headlineLarge, color = Blanco)
        Row(modifier = Modifier.fillMaxWidth()) {
            AccionMenu("Mi resumen", onVerResumen)
            AccionMenu("Ver intro", onVerIntro)
            Spacer(Modifier.weight(1f))
            AccionMenu("Salir", onCerrarSesion)
        }
        Spacer(Modifier.weight(1f))
        TarjetaJuego(
            titulo = "¿Mito o Realidad?",
            descripcion = "5 frases. ¿Verdad o mito?",
            onClick = onMitoORealidadClick
        )
        Spacer(Modifier.height(14.dp))
        TarjetaJuego(
            titulo = "Memotest",
            descripcion = "Uní cada sustancia con su dato.",
            onClick = onMemotestClick
        )
        Spacer(Modifier.height(14.dp))
        TarjetaJuego(
            titulo = "La Previa",
            descripcion = "Una noche. Tus decisiones.",
            onClick = onLaPreviaClick
        )
    }
}

@Composable
private fun AccionMenu(texto: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        modifier = Modifier.padding(end = 18.dp)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge, color = Blanco)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    TPJuegoTheme {
        HomeScreen(
            usuario = "vale",
            onMitoORealidadClick = {}, onMemotestClick = {}, onLaPreviaClick = {},
            onVerIntro = {}, onVerResumen = {}, onCerrarSesion = {}
        )
    }
}
