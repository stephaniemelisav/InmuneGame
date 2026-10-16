package com.example.tpjuego.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpjuego.ui.screens.ComoSeJuegaScreen
import com.example.tpjuego.ui.screens.GameScreen
import com.example.tpjuego.ui.screens.HomeScreen
import com.example.tpjuego.ui.screens.IntroScreen
import com.example.tpjuego.ui.screens.PortadaScreen

// Rutas de cada Activity. La app tiene tres Activities:
//  - MainActivity:          intro y menú de juegos       -> AppNavigation()
//  - MitoORealidadActivity: el juego ¿Mito o Realidad?   -> MitoORealidadNavigation()
//  - MemotestActivity:      el Memotest (una sola pantalla)
object Routes {
    const val INTRO = "intro"
    const val MENU = "menu"
    const val MITO_PORTADA = "mito/portada"
    const val MITO_COMO_SE_JUEGA = "mito/como-se-juega"
    const val MITO_JUEGO = "mito/juego"
}

/** Navegación de MainActivity: intro y menú. Los juegos se abren en otras Activities. */
@Composable
fun AppNavigation(
    onAbrirMitoORealidad: () -> Unit,
    onAbrirMemotest: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.INTRO) {
        composable(Routes.INTRO) {
            IntroScreen(
                onTerminar = {
                    // La intro sale de la pila: el botón atrás desde el menú cierra la app.
                    navController.navigate(Routes.MENU) {
                        popUpTo(Routes.INTRO) { inclusive = true }
                        // Si se volvió a ver la intro desde el menú, reusa ese menú.
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.MENU) {
            HomeScreen(
                onMitoORealidadClick = onAbrirMitoORealidad,
                onMemotestClick = onAbrirMemotest,
                onVerIntro = { navController.navigate(Routes.INTRO) }
            )
        }
    }
}

/** Navegación de MitoORealidadActivity: portada -> cómo se juega -> partida. */
@Composable
fun MitoORealidadNavigation(onTerminar: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MITO_PORTADA) {
        composable(Routes.MITO_PORTADA) {
            // "Volver al menú" cierra esta Activity y vuelve a MainActivity.
            PortadaScreen(
                onComenzar = { navController.navigate(Routes.MITO_COMO_SE_JUEGA) },
                onVolver = onTerminar
            )
        }
        composable(Routes.MITO_COMO_SE_JUEGA) {
            ComoSeJuegaScreen(
                onJugar = {
                    // Saca "Cómo se juega" de la pila: el botón atrás vuelve a la portada.
                    navController.navigate(Routes.MITO_JUEGO) {
                        popUpTo(Routes.MITO_PORTADA)
                    }
                }
            )
        }
        composable(Routes.MITO_JUEGO) {
            // "Terminar y volver al menú" cierra la Activity del juego (finish).
            GameScreen(onTerminar = onTerminar)
        }
    }
}
