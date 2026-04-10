package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.ui.theme.*

// TAREA: TransaccionesScreen con mínimo 8 movimientos + filtro por tipo funcional
@Composable
fun TransaccionesScreen(onBack: () -> Unit, numeroCuenta: String) {
    // Estado del filtro seleccionado: 0=Todos, 1=Débitos, 2=Créditos
    var filtroActivo by remember { mutableIntStateOf(0) }
    val etqFiltros   = listOf("Todos", "Débitos", "Créditos")

    // Filtrar la lista según la selección
    val transaccionesFiltradas = remember(filtroActivo) {
        when (filtroActivo) {
            1    -> DemoData.transacciones.filter { it.esDebito() }
            2    -> DemoData.transacciones.filter { !it.esDebito() }
            else -> DemoData.transacciones  // Todos (índice 0)
        }
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Movimientos", mostrarBack = true, onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Tarjeta de saldo
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Saldo disponible",
                            color    = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                        Text(
                            "S/ ${String.format("%,.2f", DemoData.cuenta.saldo)}",
                            color      = Color.White,
                            fontSize   = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))

                // TAREA: Filtro por tipo con FilterChips
                Text(
                    "Filtrar por tipo",
                    fontWeight = FontWeight.SemiBold,
                    color      = NavyDark,
                    fontSize   = 14.sp
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    etqFiltros.forEachIndexed { index, etq ->
                        FilterChip(
                            selected = filtroActivo == index,
                            onClick  = { filtroActivo = index },
                            label    = { Text(etq) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (index) {
                                    1    -> RedNegative
                                    2    -> GreenPositive
                                    else -> NavyPrimary
                                },
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "${transaccionesFiltradas.size} movimientos",
                    fontSize = 13.sp,
                    color    = GrayMedium
                )
                Spacer(Modifier.height(8.dp))
            }

            // Lista de transacciones filtradas (mínimo 8 en DemoData)
            items(transaccionesFiltradas) { transaccion ->
                FilaTransaccion(transaccion = transaccion)
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
