package com.example.tpjuego.ui.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.tpjuego.R
import com.example.tpjuego.ui.components.BotonPrimario
import com.example.tpjuego.ui.components.BotonSecundario
import com.example.tpjuego.ui.components.Etiqueta
import com.example.tpjuego.ui.components.PantallaDegradado
import com.example.tpjuego.ui.components.Tarjeta
import com.example.tpjuego.ui.theme.Amarillo
import com.example.tpjuego.ui.theme.AzulNoche
import com.example.tpjuego.ui.theme.Blanco
import com.example.tpjuego.ui.theme.TPJuegoTheme

// Portada del Memotest (misma estructura que la de ¿Mito o Realidad?)
@Composable
fun PortadaMemotestScreen(onComenzar: () -> Unit, onVolver: () -> Unit) {
    val context = LocalContext.current

    // Música de la portada (introduccion1). Se crea una sola vez.
    // isLooping = true la repite; ponelo en false si querés que suene una sola vez.
    val musica = remember {
        MediaPlayer.create(context, R.raw.introduccion1)?.apply {
            isLooping = true
        }
    }

    // Suena mientras la portada está visible, se pausa si la app pasa a segundo plano
    // y se libera cuando la portada desaparece (al tocar Comencemos o Volver al menú).
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> musica?.start()
                Lifecycle.Event.ON_PAUSE -> musica?.pause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            musica?.release()
        }
    }

    PantallaDegradado {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(32.dp))
            Etiqueta("JUEGO DE MEMORIA", fondo = Amarillo, colorTexto = AzulNoche)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "MEMOTEST",
                style = MaterialTheme.typography.displayLarge,
                color = Blanco
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Encontrá los pares.",
                style = MaterialTheme.typography.titleMedium,
                color = Blanco
            )
            Spacer(Modifier.height(24.dp))
            Tarjeta {
                Text(
                    text = "6 parejas",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Da vuelta dos cartas iguales. Cada vez que encuentres un par, " +
                            "vas a leer un dato para cuidarte. Después podés volver a leerlo.",
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
private fun PortadaMemotestScreenPreview() {
    TPJuegoTheme {
        PortadaMemotestScreen(onComenzar = {}, onVolver = {})
    }
}