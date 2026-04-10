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
import com.example.appmibancosem2.ui.theme.*

// TAREA: Dashboard con mínimo 2 TarjetaCuenta + 4 botones de acceso rápido
// + botón de logout que limpia el back stack
@Composable
fun DashboardScreen(
    onNavTransacciones: (String) -> Unit,
    onNavPagos: () -> Unit,
    onNavPrestamos: () -> Unit,
    onNavAhorro: () -> Unit,
    onLogout: () -> Unit  // NUEVO: logout para la Tarea
) {
    Scaffold(
        topBar = {
            // TopBar personalizado con botón de logout
            DashboardTopBar(onLogout = onLogout)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Saludo
            Text(
                text       = "¡Hola, ${DemoData.cuenta.titular}! 👋",
                fontSize   = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color      = NavyDark
            )

            // TAREA: Mínimo 2 TarjetaCuenta en el Dashboard
            Text("Mis cuentas", fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 16.sp)
            // Tarjeta 1 — Cuenta de Ahorros
            TarjetaCuenta(cuenta = DemoData.cuenta)
            // Tarjeta 2 — Cuenta Corriente
            TarjetaCuenta(cuenta = DemoData.cuentaCorriente)

            // TAREA: 4 botones de acceso rápido con BotonAccesoRapido
            Text("Accesos rápidos", fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 16.sp)
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // En DashboardScreen, cambiar la llamada a onNavTransacciones:
                BotonAccesoRapido(
                    icono    = Icons.Default.Receipt,
                    etiqueta = "Movimientos",
                    onClick  = { onNavTransacciones(DemoData.cuenta.numero) }  // pasa número de cuenta
                )

                BotonAccesoRapido(icono = Icons.Default.Payment,         etiqueta = "Pagos",       onClick = onNavPagos)
                BotonAccesoRapido(icono = Icons.Default.AccountBalance,  etiqueta = "Préstamos",   onClick = onNavPrestamos)
                BotonAccesoRapido(icono = Icons.Default.Savings,         etiqueta = "Ahorro",      onClick = onNavAhorro)
            }

            // Últimos movimientos
            Text("Últimos movimientos", fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 16.sp)
            Card(elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    DemoData.transacciones.take(3).forEach { FilaTransaccion(it) }
                    TextButton(
                        onClick = { onNavTransacciones(DemoData.cuenta.numero) }, 
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver todos los movimientos →", color = NavyPrimary, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

// TopBar personalizado para Dashboard con botón de logout
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar(onLogout: () -> Unit) {
    TopAppBar(
        title = {
            Text("MiBanco", color = Color.White, fontWeight = FontWeight.Bold)
        },
        actions = {
            // Botón de logout en la barra superior derecha
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
