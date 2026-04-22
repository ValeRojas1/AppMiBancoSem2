package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.navigation.Screen   // ← IMPORT FALTANTE
import com.example.appmibancosem2.ui.theme.*

@Composable
fun DashboardScreen(
    onNavigateTo : (Screen) -> Unit,   // ← UN SOLO callback unificado
    onLogout     : () -> Unit
) {
    Scaffold(
        topBar = { DashboardTopBar(onLogout = onLogout) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text       = "¡Hola, ${DemoData.cuenta.titular}! 👋",
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NavyDark
            )

            Text("Mis cuentas", fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 16.sp)
            TarjetaCuenta(cuenta = DemoData.cuenta)
            TarjetaCuenta(cuenta = DemoData.cuentaCorriente)

            // Primera fila: 4 botones originales
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BotonAccesoRapido(
                    icono    = Icons.Default.Receipt,
                    etiqueta = "Movimientos",
                    onClick  = { onNavigateTo(Screen.Transacciones) }
                )
                BotonAccesoRapido(
                    icono    = Icons.Default.Payment,
                    etiqueta = "Pagar",
                    onClick  = { onNavigateTo(Screen.Pagos) }
                )
                BotonAccesoRapido(
                    icono    = Icons.Default.AccountBalance,
                    etiqueta = "Préstamos",
                    onClick  = { onNavigateTo(Screen.Prestamos) }
                )
                BotonAccesoRapido(
                    icono    = Icons.Default.Savings,
                    etiqueta = "Ahorro",
                    onClick  = { onNavigateTo(Screen.Ahorro) }
                )
            }

            Spacer(Modifier.height(4.dp))

            // Segunda fila: botón Crédito centrado
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                BotonAccesoRapido(
                    icono    = Icons.Default.RequestPage,
                    etiqueta = "Crédito",
                    onClick  = { onNavigateTo(Screen.SolicitudCredito) }
                )
            }

            Text("Últimos movimientos", fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 16.sp)
            Card(elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    DemoData.transacciones.take(3).forEach { FilaTransaccion(it) }
                    TextButton(
                        onClick  = { onNavigateTo(Screen.Transacciones) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver todos los movimientos →", color = NavyPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(onLogout: () -> Unit) {
    TopAppBar(
        title   = { Text("MiBanco", color = Color.White, fontWeight = FontWeight.Bold) },
        actions = {
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión",
                    tint               = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyPrimary)
    )
}