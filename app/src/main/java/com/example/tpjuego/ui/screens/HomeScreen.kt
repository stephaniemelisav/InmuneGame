package com.example.tpjuego.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Pantalla de inicio provisoria. Se rehace siguiendo el diseño de Figma.
@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onMemotestClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "INMUNE GAME 7",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Pantalla de inicio",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = onPlayClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comience a jugar")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onMemotestClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Memotest")
            }
        }
    }
}

//Con el preview mostramos en el panel split, útil para desarrollar estos componentes y visualizar la previa
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    TPJuegoTheme {
        HomeScreen(onPlayClick = {}, onMemotestClick = {})
    }
}