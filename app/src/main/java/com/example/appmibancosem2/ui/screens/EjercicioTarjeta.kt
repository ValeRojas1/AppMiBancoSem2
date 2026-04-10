package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// EJERCICIO 1 — TarjetaCuentaCustom
// Box (contenedor degradado) + Column (organización vertical)
// + Row (badge + icono) — patrón ConstraintLayout equivalente en Compose
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TarjetaCuentaCustom(
    nombre: String,
    numero: String,
    saldo: Double,
    tipo: String,
    modifier: Modifier = Modifier
) {
    // Card: contenedor con sombra y bordes redondeados
    Card(
        modifier  = modifier
            .fillMaxWidth()
            .height(150.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // Box: permite superponer elementos (fondo degradado + contenido)
        Box(
            modifier = Modifier
                .fillMaxSize()
                // TODO 2: Degradado horizontal NavyPrimary → NavyMid
                .background(
                    Brush.horizontalGradient(listOf(NavyPrimary, NavyMid))
                )
                .padding(20.dp)
        ) {
            // Column con SpaceBetween: distribuye verticalmente todo el espacio
            Column(
                modifier            = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // TODO 3: Row superior — badge de tipo + icono CreditCard
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    // Badge de tipo de cuenta
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = GoldAccent.copy(alpha = 0.9f)
                    ) {
                        Text(
                            text     = tipo.uppercase(),
                            color    = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            letterSpacing = 1.sp
                        )
                    }
                    // Ícono de tarjeta en la esquina superior derecha
                    Icon(
                        imageVector        = Icons.Default.CreditCard,
                        contentDescription = "Tarjeta bancaria",
                        tint               = Color.White.copy(alpha = 0.8f),
                        modifier           = Modifier.size(26.dp)
                    )
                }

                // Número enmascarado: muestra ****xxxx (últimos 4 dígitos)
                // takeLast(4) toma los últimos 4 caracteres del número
                Text(
                    text  = "**** **** ${numero.takeLast(4)}",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    letterSpacing = 2.sp
                )

                // Parte inferior: nombre y saldo
                Column {
                    Text(
                        text     = nombre,
                        color    = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    // TODO 4: Saldo formateado S/ x,xxx.xx
                    Text(
                        text       = "S/ %,.2f".format(saldo),
                        color      = Color.White,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text     = "Saldo disponible",
                        color    = GoldLight.copy(alpha = 0.9f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// Preview — permite ver el componente en Android Studio sin ejecutar la app
@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
fun PreviewTarjetaCuentaCustom() {
    // Datos ficticios de prueba según las instrucciones del ejercicio
    TarjetaCuentaCustom(
        nombre = "Carlos Lopez",
        numero = "0194521",
        saldo  = 4250.0,
        tipo   = "corriente"
    )
}
