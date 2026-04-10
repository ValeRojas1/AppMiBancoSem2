package com.example.appmibancosem2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.appmibancosem2.navigation.MiBancoNavGraph
import com.example.appmibancosem2.ui.theme.Appmibancosem2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()    // El contenido llega hasta los bordes de la pantalla
        setContent {
            Appmibancosem2Theme {
                // rememberNavController: crea y recuerda el controlador de navegación
                // entre recomposiciones (no se pierde al rotar la pantalla)
                val navController = rememberNavController()
                MiBancoNavGraph(navController = navController)
            }
        }
    }
}
