package com.example.tpjuego.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpjuego.ui.screens.ComoSeJuegaScreen
import com.example.tpjuego.ui.screens.GameScreen
import com.example.tpjuego.ui.screens.HomeScreen
import com.example.tpjuego.ui.screens.IntroScreen
import com.example.tpjuego.ui.screens.MemotestScreen
import com.example.tpjuego.ui.screens.PortadaScreen

// Rutas de la app. Para el segundo juego se suman acá sus pantallas.
object Routes {
    const val INTRO = "intro"
    const val MENU = "menu"
    const val MITO_PORTADA = "mito/portada"
    const val MITO_COMO_SE_JUEGA = "mito/como-se-juega"
    const val MITO_JUEGO = "mito/juego"
    const val MEMOTEST = "memotest"
}

@Composable
fun AppNavigation() {
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
                onMitoORealidadClick = { navController.navigate(Routes.MITO_PORTADA) },
                onMemotestClick = { navController.navigate(Routes.MEMOTEST) },
                onVerIntro = { navController.navigate(Routes.INTRO) }
            )
        }
        composable(Routes.MITO_PORTADA) {
            PortadaScreen(
                onComenzar = { navController.navigate(Routes.MITO_COMO_SE_JUEGA) },
                onVolver = { navController.popBackStack() }
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
            GameScreen(
                // "Terminar" cierra el juego y vuelve al menú de la app.
                onTerminar = { navController.popBackStack(Routes.MENU, inclusive = false) }
            )
        }
        composable(Routes.MEMOTEST) {
            MemotestScreen(onVolver = { navController.popBackStack(Routes.MENU, inclusive = false) })
        }
    }
}
