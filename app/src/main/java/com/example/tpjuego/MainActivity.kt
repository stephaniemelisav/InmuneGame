package com.example.tpjuego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.tpjuego.ui.AppNavigation
import com.example.tpjuego.ui.theme.TPJuegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TPJuegoTheme {
                AppNavigation()
            }
        }
    }
}
