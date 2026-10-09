package com.example.tpjuego.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// Creamos la clase carta
data class Carta(
    val parejaId: Int,
    val texto: String,
    val volteada: Boolean = false,
    val encontrada: Boolean = false
)

// Unimos las cartas con su par correspondiente
private val pares = listOf(
    "Alcohol" to "Deprime el sistema nervioso central",
    "Tabaco" to "La nicotina genera alta dependencia",
    "Marihuana" to "Afecta la memoria y la atención",
    "Energizante + alcohol" to "Disimula la intoxicación y es riesgoso",
    "Cocaína" to "Aumenta el riesgo de problemas cardíacos",
    "Pedir ayuda" to "Hablar con alguien de confianza es el primer paso"
)

// Arma la lista con la pareja de cartas y las mezcla
private fun generarCartas(): List<Carta> =
    pares.flatMapIndexed { i, (concepto, dato) ->
        listOf(Carta(i, concepto), Carta(i, dato))
    }.shuffled()

@Composable
fun MemotestScreen(onVolver: () -> Unit = {}) {
    var cartas by remember { mutableStateOf(generarCartas()) } // Usamos la variable directamente
    var seleccion by remember { mutableStateOf(listOf<Int>()) } // Muestra las cartas dadas vuelta en ese turno
    var intentos by remember { mutableIntStateOf(0) } // Muestra el contador

    // Cuando hay 2 cartas dadas vuelta, las comparamos
    LaunchedEffect(seleccion) {
        if (seleccion.size == 2) {
            val (a, b) = seleccion
            intentos++
            if (cartas[a].parejaId == cartas[b].parejaId) {
                cartas = cartas.toMutableList().also {
                    it[a] = it[a].copy(encontrada = true)
                    it[b] = it[b].copy(encontrada = true)
                }
            } else {
                delay(1000)
                cartas = cartas.toMutableList().also {
                    it[a] = it[a].copy(volteada = false)
                    it[b] = it[b].copy(volteada = false)
                }
            }
            seleccion = emptyList()
        }
    }

    // Se termina si todas las cartas fueron encontradas
    val terminado = cartas.all { it.encontrada }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Memotest", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Intentos: $intentos", fontSize = 18.sp)
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(cartas) { index, carta ->
                CartaItem(carta) {
                    // Ignora toques si ya hay 2 dadas vuelta o la carta ya está visible
                    if (seleccion.size < 2 && !carta.volteada && !carta.encontrada) {
                        cartas = cartas.toMutableList().also {
                            it[index] = it[index].copy(volteada = true)
                        }
                        seleccion = seleccion + index
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onVolver) { Text("Volver") }
    }

    if (terminado) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("¡Completaste el juego!") },
            text = { Text("Lo lograste en $intentos intentos. Recordá: informarte es cuidarte.") },
            confirmButton = {
                TextButton(onClick = {
                    cartas = generarCartas()
                    seleccion = emptyList()
                    intentos = 0
                }) { Text("Jugar de nuevo") }
            },
            dismissButton = {
                TextButton(onClick = onVolver) { Text("Voler al Menu") }
            }
        )
    }
}

@Composable
private fun CartaItem(carta: Carta, onClick: () -> Unit) {
    // Elije el color segun el estado de la carta
    val colorFondo = when {
        carta.encontrada -> Color(0xFF2E7D32) // verde
        carta.volteada -> Color(0xFFF57C00)   // naranja
        else -> Color(0xFF3F51B5)             // azul
    }
    Box(
        modifier = Modifier
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo)
            .clickable(onClick = onClick)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (carta.volteada || carta.encontrada) carta.texto else "?",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}