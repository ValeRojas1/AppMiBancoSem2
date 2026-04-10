package com.example.appmibancosem2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MiBancoColorScheme = lightColorScheme(
    primary          = NavyPrimary,    // Botones, elementos interactivos
    secondary        = GoldAccent,     // Elementos secundarios, badges
    tertiary         = NavyLight,      // Acentos terciarios
    background       = GrayLight,      // Fondo de la app
    surface          = Color.White,    // Fondo de Cards
    onPrimary        = Color.White,    // Texto sobre elementos primarios
    onSecondary      = Color.White,    // Texto sobre elementos secundarios
    onBackground     = NavyDark,       // Texto sobre el fondo
    onSurface        = NavyDark,       // Texto sobre Cards
    error            = RedNegative,    // Mensajes de error
)

@Composable
fun Appmibancosem2Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MiBancoColorScheme,
        typography  = Typography,
        content     = content
    )
}
