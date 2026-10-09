package com.example.tpjuego.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tpjuego.ui.screens.GameScreen
import com.example.tpjuego.ui.screens.HomeScreen
import com.example.tpjuego.ui.screens.MemotestScreen

// Rutas de la app. Cuando sumemos pantallas del Figma, se agregan acá.
object Routes {
    const val HOME = "home"
    const val GAME = "game"
    const val MEMOTEST = "memotest"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlayClick = { navController.navigate(Routes.GAME) },
                onMemotestClick = { navController.navigate(Routes.MEMOTEST) }
            )
        }
        composable(Routes.GAME) {
            GameScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.MEMOTEST) {
            MemotestScreen(onVolver = { navController.popBackStack() })
        }
    }
}