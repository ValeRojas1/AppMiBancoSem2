package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// EJERCICIO 4 — Simulador de Préstamo con Amortización
// Fórmula francesa: C = P × [r(1+r)^n] / [(1+r)^n - 1]
// ─────────────────────────────────────────────────────────────────────────────

// Función de cálculo — separada del Composable para poder probarse unitariamente
// Verificación: S/5,000 | 12m | 24% = S/472.89 mensual
fun calcularCuota(monto: Double, plazoMeses: Int, tasaAnual: Double): Double {
    val r = tasaAnual / 100.0 / 12.0  // tasa mensual en decimal (ej: 24%/12 = 0.02)
    val n = plazoMeses.toDouble()
    if (r == 0.0) return monto / n    // caso borde: sin intereses
    val factor = Math.pow(1 + r, n)   // (1+r)^n
    return monto * (r * factor) / (factor - 1) // fórmula completa
}

// DESAFÍO: Genera la tabla de las primeras N cuotas de amortización
// Cada cuota: capital amortizado (va subiendo) + interés del mes (va bajando)
data class FilaCuota(
    val mes: Int,
    val cuota: Double,
    val capital: Double,   // porción que reduce la deuda
    val interes: Double,   // porción que va al banco
    val saldoRestante: Double
)

fun generarTablaAmortizacion(
    monto: Double,
    plazoMeses: Int,
    tasaAnual: Double,
    numFilas: Int = 6  // mostrar las primeras 6 cuotas
): List<FilaCuota> {
    val r     = tasaAnual / 100.0 / 12.0
    val cuota = calcularCuota(monto, plazoMeses, tasaAnual)
    var saldo = monto
    val tabla = mutableListOf<FilaCuota>()

    for (mes in 1..minOf(numFilas, plazoMeses)) {
        val interesMes  = saldo * r                 // interés = saldo × tasa mensual
        val capitalMes  = cuota - interesMes        // capital = cuota - interés
        saldo          -= capitalMes                // se reduce la deuda
        tabla.add(FilaCuota(mes, cuota, capitalMes, interesMes, maxOf(0.0, saldo)))
    }
    return tabla
}

@Composable
fun PrestamosScreen(onBack: () -> Unit) {
    // Opciones disponibles
    val plazos    = listOf(6, 12, 24, 36)
    val tasas     = listOf(18.0, 24.0, 30.0)
    val etqTasas  = listOf("18%", "24%", "30%")

    // Estado del simulador
    var monto      by remember { mutableFloatStateOf(5000f) }
    var plazoIndex by remember { mutableIntStateOf(1) }   // 12 meses por defecto
    var tasaIndex  by remember { mutableIntStateOf(1) }   // 24% por defecto

    // derivedStateOf: solo recalcula cuando cambian las dependencias
    // Sin esto se recalcularía en CADA recomposición (ineficiente)
    val cuota        by remember(monto, plazoIndex, tasaIndex) {
        derivedStateOf {
            calcularCuota(monto.toDouble(), plazos[plazoIndex], tasas[tasaIndex])
        }
    }
    val totalPagar   by remember(cuota, plazoIndex) {
        derivedStateOf { cuota * plazos[plazoIndex] }
    }
    val totalInteres by remember(totalPagar, monto) {
        derivedStateOf { totalPagar - monto.toDouble() }
    }
    val tablaAmort   by remember(monto, plazoIndex, tasaIndex) {
        derivedStateOf {
            generarTablaAmortizacion(monto.toDouble(), plazos[plazoIndex], tasas[tasaIndex])
        }
    }

    val fmt = NumberFormat.getNumberInstance(Locale("es", "PE")).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo      = "Simulador de Préstamos",
                mostrarBack = true,
                onBack      = onBack
            )
        }
    ) { paddingValues ->
        // LazyColumn para todo el contenido: permite scroll y es más eficiente
        // que Column + verticalScroll cuando hay tablas con muchos items
        LazyColumn(
            modifier       = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Slider de monto ────────────────────────────────────────────
            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monto del préstamo", fontWeight = FontWeight.Medium, color = NavyDark)
                            Text(
                                "S/ ${fmt.format(monto)}",
                                fontWeight = FontWeight.Bold,
                                color      = NavyPrimary,
                                fontSize   = 18.sp
                            )
                        }
                        Slider(
                            value         = monto,
                            onValueChange = { monto = it },
                            valueRange    = 1000f..50000f,
                            steps         = 98,
                            colors        = SliderDefaults.colors(activeTrackColor = NavyPrimary),
                            modifier      = Modifier.fillMaxWidth()
                        )
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("S/ 1,000", fontSize = 11.sp, color = GrayMedium)
                            Text("S/ 50,000", fontSize = 11.sp, color = GrayMedium)
                        }
                    }
                }
            }

            // ── Chips de plazo ─────────────────────────────────────────────
            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Plazo (meses)", fontWeight = FontWeight.Medium, color = NavyDark)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            plazos.forEachIndexed { i, p ->
                                FilterChip(
                                    selected = plazoIndex == i,
                                    onClick  = { plazoIndex = i },
                                    label    = { Text("${p}m", fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NavyPrimary,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ── Chips de tasa ──────────────────────────────────────────────
            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tasa de interés anual (TEA)", fontWeight = FontWeight.Medium, color = NavyDark)
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            etqTasas.forEachIndexed { i, t ->
                                FilterChip(
                                    selected = tasaIndex == i,
                                    onClick  = { tasaIndex = i },
                                    label    = { Text(t, fontSize = 13.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors   = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GoldAccent,
                                        selectedLabelColor     = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // ── Card de resultados ─────────────────────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    colors    = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Cuota mensual estimada", color = Color.White.copy(alpha = 0.8f))
                        Spacer(Modifier.height(6.dp))
                        // La cuota mensual es el número más grande (24sp)
                        Text(
                            text       = "S/ ${fmt.format(cuota)}",
                            fontSize   = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color      = GoldLight
                        )
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                        Spacer(Modifier.height(16.dp))
                        // Los 3 resultados en fila
                        Row(
                            modifier              = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultadoItem("Total a pagar",   "S/ ${fmt.format(totalPagar)}")
                            ResultadoItem("Intereses",        "S/ ${fmt.format(totalInteres)}")
                            ResultadoItem("Plazo",            "${plazos[plazoIndex]}m / ${tasas[tasaIndex]}%")
                        }
                    }
                }
            }

            // ── DESAFÍO: Tabla de amortización ─────────────────────────────
            item {
                Text(
                    "Tabla de amortización (primeras 6 cuotas)",
                    fontWeight = FontWeight.SemiBold,
                    color      = NavyDark,
                    fontSize   = 15.sp
                )
            }

            // Encabezado de la tabla
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                    shape  = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TablaHeader("Mes", Modifier.weight(0.5f))
                        TablaHeader("Cuota", Modifier.weight(1.2f))
                        TablaHeader("Capital", Modifier.weight(1.2f))
                        TablaHeader("Interés", Modifier.weight(1.2f))
                        TablaHeader("Saldo", Modifier.weight(1.2f))
                    }
                }
            }

            // Filas de la tabla
            itemsIndexed(tablaAmort) { index, fila ->
                Card(
                    shape  = if (index == tablaAmort.lastIndex)
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    else
                        RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
                        // Filas alternas para mejor legibilidad
                        containerColor = if (index % 2 == 0) Color.White else NavyPrimary.copy(alpha = 0.04f)
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        TablaCell("${fila.mes}", Modifier.weight(0.5f), fontWeight = FontWeight.Bold, color = NavyPrimary)
                        TablaCell(fmt.format(fila.cuota), Modifier.weight(1.2f))
                        TablaCell(fmt.format(fila.capital), Modifier.weight(1.2f), color = NavyPrimary)
                        TablaCell(fmt.format(fila.interes), Modifier.weight(1.2f), color = RedNegative)
                        TablaCell(fmt.format(fila.saldoRestante), Modifier.weight(1.2f))
                    }
                }
            }

            item {
                Text(
                    "* Cálculo referencial. S/5,000 | 12m | 24% = S/472.89 mensual",
                    fontSize = 11.sp,
                    color    = GrayMedium
                )
            }
        }
    }
}

// Componentes auxiliares para la tabla
@Composable
private fun ResultadoItem(etiqueta: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(etiqueta, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
        Text(valor, color = GoldLight, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun TablaHeader(texto: String, modifier: Modifier) {
    Text(
        text       = texto,
        modifier   = modifier,
        color      = Color.White,
        fontSize   = 11.sp,
        fontWeight = FontWeight.Bold,
        textAlign  = TextAlign.Center
    )
}

@Composable
private fun TablaCell(
    texto: String,
    modifier: Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    color: androidx.compose.ui.graphics.Color = NavyDark
) {
    Text(
        text       = texto,
        modifier   = modifier,
        fontSize   = 11.sp,
        fontWeight = fontWeight,
        color      = color,
        textAlign  = TextAlign.Center
    )
}
