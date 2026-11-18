package com.example.tpjuego.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tpjuego.data.AfirmacionesRepositorio
import com.example.tpjuego.data.EstadoRanking
import com.example.tpjuego.data.FilaRanking
import com.example.tpjuego.data.Juego
import com.example.tpjuego.game.RankingViewModel
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.NaranjaEstado
import com.example.tpjuego.ui.theme.Turquesa

/** Ranking compartido: el mejor resultado de cada jugador, leído de Cloud Firestore. */
@Composable
fun RankingScreen(onVolver: () -> Unit, viewModel: RankingViewModel = viewModel()) {
    val juego by viewModel.juego.collectAsState()
    val estado by viewModel.estado.collectAsState()

    PantallaDegradado {
        Spacer(Modifier.height(16.dp))
        Text("RANKING", style = MaterialTheme.typography.headlineLarge, color = Blanco)
        Text(
            if (juego.mayorEsMejor) "MÁS ACIERTOS ARRIBA" else "MENOS INTENTOS ARRIBA",
            style = MaterialTheme.typography.titleSmall,
            color = Blanco
        )
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            viewModel.juegos.forEachIndexed { i, j ->
                if (i > 0) Spacer(Modifier.width(8.dp))
                PestaniaJuego(
                    texto = if (j == Juego.MITO_O_REALIDAD) "MITO O REALIDAD" else "MEMOTEST",
                    activa = j == juego,
                    modifier = Modifier.weight(1f)
                ) { viewModel.elegir(j) }
            }
        }
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Tarjeta {
                when (val e = estado) {
                    EstadoRanking.Cargando -> Text("Cargando ranking…", style = MaterialTheme.typography.bodyLarge)
                    EstadoRanking.NoConfigurado -> Text(
                        "El ranking no está configurado (falta el archivo de Firebase).",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    is EstadoRanking.Error -> {
                        Text(e.mensaje, style = MaterialTheme.typography.bodyLarge, color = NaranjaEstado)
                        TextButton(onClick = viewModel::cargar) {
                            Text("Reintentar", style = MaterialTheme.typography.labelLarge, color = Turquesa)
                        }
                    }
                    is EstadoRanking.Listo -> {
                        if (e.sinConexion) {
                            Text(
                                "Sin conexión: se muestran los últimos datos guardados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NaranjaEstado
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        if (e.filas.isEmpty()) {
                            Text("Todavía nadie jugó. ¡Sé el primero!", style = MaterialTheme.typography.bodyLarge)
                        } else {
                            e.filas.forEachIndexed { i, fila ->
                                if (i > 0) HorizontalDivider(color = AzulNoche.copy(alpha = 0.12f))
                                FilaDeRanking(fila, juego, esVos = fila.usuario == viewModel.usuario)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        BotonSecundario("Volver al menú", onClick = onVolver)
    }
}

@Composable
private fun PestaniaJuego(texto: String, activa: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (activa) Amarillo else Blanco.copy(alpha = 0.15f))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, style = MaterialTheme.typography.titleSmall, color = if (activa) AzulNoche else Blanco)
    }
}

@Composable
private fun FilaDeRanking(fila: FilaRanking, juego: Juego, esVos: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (fila.posicion <= 3) Amarillo else AzulNoche.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Text("${fila.posicion}", style = MaterialTheme.typography.titleSmall, color = AzulNoche)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            if (esVos) "${fila.usuario} (vos)" else fila.usuario,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (esVos) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        Text(
            when (juego) {
                Juego.MITO_O_REALIDAD -> "${fila.resultado}/${AfirmacionesRepositorio.PREGUNTAS_POR_PARTIDA}"
                else -> "${fila.resultado} intentos"
            },
            style = MaterialTheme.typography.titleSmall,
            color = AzulNoche
        )
    }
}
