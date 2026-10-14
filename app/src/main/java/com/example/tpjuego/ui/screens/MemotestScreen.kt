package com.example.tpjuego.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme
import com.example.tpjuego.ui.theme.Turquesa
import com.example.tpjuego.ui.theme.VerdeEstado
import kotlinx.coroutines.delay

// Memotest: juego creado por Enzo. Integrado al menú y adaptado al Design System.

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
    val paresEncontrados = cartas.count { it.encontrada } / 2

    if (terminado) {
        // Pantalla final con el resumen, igual que en ¿Mito o Realidad?
        MemotestFinal(
            intentos = intentos,
            onJugarDeNuevo = {
                cartas = generarCartas()
                seleccion = emptyList()
                intentos = 0
            },
            onVolver = onVolver
        )
        return
    }

    PantallaDegradado {
        Text("MEMOTEST", style = MaterialTheme.typography.headlineLarge, color = Blanco)
        Spacer(Modifier.height(4.dp))
        Text(
            "Uní cada sustancia con su dato.",
            style = MaterialTheme.typography.bodyMedium,
            color = Blanco
        )
        Spacer(Modifier.height(8.dp))
        // Contador de intentos y pares encontrados
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                "INTENTOS: $intentos",
                style = MaterialTheme.typography.labelMedium,
                color = Blanco,
                modifier = Modifier.weight(1f)
            )
            Text(
                "PARES: $paresEncontrados / ${pares.size}",
                style = MaterialTheme.typography.labelMedium,
                color = Amarillo
            )
        }
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

        Spacer(Modifier.height(12.dp))
        BotonSecundario("Terminar y volver al menú", onClick = onVolver)
    }
}

@Composable
private fun CartaItem(carta: Carta, onClick: () -> Unit) {
    // Elije el color segun el estado de la carta (con los colores del Design System)
    val colorFondo = when {
        carta.encontrada -> VerdeEstado
        carta.volteada -> Blanco
        else -> Turquesa
    }
    val colorTexto = if (carta.volteada && !carta.encontrada) AzulNoche else Blanco
    Box(
        modifier = Modifier
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colorFondo)
            .clickable(onClick = onClick)
            .padding(6.dp)
            .semantics {
                contentDescription = when {
                    carta.encontrada -> "Par encontrado: ${carta.texto}"
                    carta.volteada -> carta.texto
                    else -> "Carta boca abajo"
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (carta.volteada || carta.encontrada) carta.texto else "?",
            color = colorTexto,
            fontSize = if (carta.volteada || carta.encontrada) 13.sp else 32.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MemotestFinal(intentos: Int, onJugarDeNuevo: () -> Unit, onVolver: () -> Unit) {
    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text(
                "¡COMPLETASTE EL JUEGO!",
                style = MaterialTheme.typography.headlineLarge,
                color = Blanco,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Tarjeta(alineacion = Alignment.CenterHorizontally) {
                Text("$intentos", style = MaterialTheme.typography.displayLarge)
                Text("intentos para encontrar ${pares.size} pares", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Recordá: informarte es cuidarte.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(16.dp))
            Tarjeta {
                Text("LO QUE APRENDISTE", style = MaterialTheme.typography.titleSmall)
                pares.forEach { (concepto, dato) ->
                    Spacer(Modifier.height(10.dp))
                    Text(concepto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(dato, style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
        BotonPrimario("Jugar de nuevo", onClick = onJugarDeNuevo)
        Spacer(Modifier.height(12.dp))
        BotonSecundario("Terminar y volver al menú", onClick = onVolver)
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MemotestPreview() {
    TPJuegoTheme { MemotestScreen() }
}
