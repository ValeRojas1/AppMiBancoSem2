package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.data.model.Movimiento
import com.example.appmibancosem2.ui.theme.*

// ─────────────────────────────────────────────────────────────────────────────
// EJERCICIO 2 — ListaMovimientos + ItemMovimiento
// LazyColumn: virtualiza la lista, solo renderiza los elementos visibles
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ListaMovimientos(movimientos: List<Movimiento>) {
    // BONUS: Contar débitos y créditos usando count()
    val totalDebitos  = movimientos.count { it.tipo == "debito" }
    val totalCreditos = movimientos.count { it.tipo == "credito" }

    // LazyColumn = equivalente a RecyclerView en Compose
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // item {} = un elemento fijo (no de la lista) — el contador BONUS
        item {
            // BONUS: Contador de débitos y créditos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors   = CardDefaults.cardColors(containerColor = NavyPrimary.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Contador débitos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = "$totalDebitos débitos",
                            color      = RedNegative,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                        Text("egresos", color = GrayMedium, fontSize = 11.sp)
                    }
                    // Separador vertical
                    VerticalDivider(
                        modifier  = Modifier.height(36.dp),
                        color     = GrayMedium.copy(alpha = 0.4f),
                        thickness = 1.dp
                    )
                    // Contador créditos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = "$totalCreditos créditos",
                            color      = GreenPositive,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 14.sp
                        )
                        Text("ingresos", color = GrayMedium, fontSize = 11.sp)
                    }
                }
            }
        }

        // items() itera sobre la lista pasando cada elemento como lambda
        items(movimientos) { mov ->
            ItemMovimiento(movimiento = mov)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ItemMovimiento — fila de un movimiento individual
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ItemMovimiento(movimiento: Movimiento) {
    val esDebito   = movimiento.tipo == "debito"
    // Color del monto según tipo: rojo para débito, verde para crédito
    val colorMonto = if (esDebito) RedNegative else GreenPositive
    // Prefijo con signo correspondiente
    val prefijo    = if (esDebito) "- S/ " else "+ S/ "

    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícono circular de color (débito = flecha arriba roja, crédito = flecha abajo verde)
        Box(
            modifier           = Modifier
                .size(40.dp)
                .background(
                    color = colorMonto.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment   = Alignment.Center
        ) {
            Icon(
                imageVector = if (esDebito) Icons.Default.ArrowUpward
                else          Icons.Default.ArrowDownward,
                contentDescription = if (esDebito) "Débito" else "Crédito",
                tint     = colorMonto,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))

        // Descripción del movimiento (ocupa el espacio restante con weight)
        Text(
            text       = movimiento.descripcion,
            modifier   = Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            fontSize   = 14.sp,
            color      = NavyDark
        )

        // Monto coloreado con prefijo
        Text(
            text       = "$prefijo${"%.2f".format(movimiento.monto)}",
            color      = colorMonto,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp
        )
    }
    // Divider entre cada fila
    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
}

// Preview de la lista completa
@Preview(showBackground = true)
@Composable
fun PreviewListaMovimientos() {
    ListaMovimientos(movimientos = DemoData.movimientos)
}
