package com.example.tpjuego.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpjuego.ui.screens.GameScreen
import com.example.tpjuego.ui.screens.HomeScreen
import com.example.tpjuego.ui.screens.IntroScreen
import com.example.tpjuego.ui.screens.PortadaScreen
import com.example.tpjuego.ui.screens.RankingScreen
import com.example.tpjuego.ui.screens.ResumenScreen

// Rutas de cada Activity. La app tiene cinco Activities:
//  - LoginActivity:         ingreso con usuario y contraseña
//  - MainActivity:          intro, menú de juegos y resumen -> AppNavigation()
//  - MitoORealidadActivity: el juego ¿Mito o Realidad?   -> MitoORealidadNavigation()
//  - MemotestActivity:      el Memotest (una sola pantalla)
//  - LaPreviaActivity:      La Previa, modo historia (una sola pantalla con fases)
object Routes {
    const val INTRO = "intro"
    const val MENU = "menu"
    const val RESUMEN = "resumen"
    const val RANKING = "ranking"
    const val MITO_PORTADA = "mito/portada"
    const val MITO_JUEGO = "mito/juego"
}

/** Navegación de MainActivity: intro, menú, resumen y ranking. Los juegos se abren en otras Activities. */
@Composable
fun AppNavigation(
    usuario: String,
    onAbrirMitoORealidad: () -> Unit,
    onAbrirMemotest: () -> Unit,
    onAbrirLaPrevia: () -> Unit,
    onCerrarSesion: () -> Unit
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
                usuario = usuario,
                onMitoORealidadClick = onAbrirMitoORealidad,
                onMemotestClick = onAbrirMemotest,
                onLaPreviaClick = onAbrirLaPrevia,
                onVerIntro = { navController.navigate(Routes.INTRO) },
                onVerResumen = { navController.navigate(Routes.RESUMEN) },
                onVerRanking = { navController.navigate(Routes.RANKING) },
                onCerrarSesion = onCerrarSesion
            )
        }
        composable(Routes.RESUMEN) {
            // Los datos vienen de ResumenViewModel (Room + Firestore).
            ResumenScreen(onVolver = { navController.popBackStack() })
        }
        composable(Routes.RANKING) {
            RankingScreen(onVolver = { navController.popBackStack() })
        }
    }
}

/** Navegación de MitoORealidadActivity: portada (con las reglas) -> partida. */
@Composable
fun MitoORealidadNavigation(onTerminar: () -> Unit) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.MITO_PORTADA) {
        composable(Routes.MITO_PORTADA) {
            // "Volver al menú" cierra esta Activity y vuelve a MainActivity.
            PortadaScreen(
                onComenzar = {
                    // Saca la portada de la pila: el botón atrás desde el juego vuelve al menú.
                    navController.navigate(Routes.MITO_JUEGO) {
                        popUpTo(Routes.MITO_PORTADA) { inclusive = true }
                    }
                },
                onVolver = onTerminar
            )
        }
        composable(Routes.MITO_JUEGO) {
            // "Terminar y volver al menú" cierra la Activity del juego (finish).
            GameScreen(onTerminar = onTerminar)
        }
    }
}
