package com.example.appmibancosem2.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.ui.theme.*
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// EJERCICIO 5 — MetaAhorro con proyección
// LinearProgressIndicator animado + tabla de proyección de 6 meses
// ─────────────────────────────────────────────────────────────────────────────

// Función de cálculo de meses separada del Composable (testeable)
// Datos prueba: saldo=12875.50 | meta=20000 | tasa=3.5% | deposito=500 → ~13 meses
fun calcularMesesParaMeta(
    saldoActual: Double,
    metaAhorro: Double,
    tasaAnual: Double,
    depositoMensual: Double
): Int {
    val tasaMensual = tasaAnual / 100.0 / 12.0
    var saldo = saldoActual
    var meses = 0
    // Itera hasta alcanzar la meta o llegar a 30 años (360 meses máx para evitar bucle infinito)
    while (saldo < metaAhorro && meses < 360) {
        val interes = saldo * tasaMensual
        saldo += interes + depositoMensual
        meses++
    }
    return meses
}

// Genera la tabla de proyección para los próximos N meses
data class FilaProyeccion(
    val mes: String,
    val deposito: Double,
    val interes: Double,
    val saldoProyectado: Double
)

fun generarProyeccion(
    saldoActual: Double,
    tasaAnual: Double,
    depositoMensual: Double,
    numMeses: Int = 6
): List<FilaProyeccion> {
    val tasaMensual = tasaAnual / 100.0 / 12.0
    var saldo       = saldoActual
    val proyeccion  = mutableListOf<FilaProyeccion>()
    val hoy         = LocalDate.now()

    for (i in 1..numMeses) {
        val fecha     = hoy.plusMonths(i.toLong())
        val interes   = saldo * tasaMensual
        saldo        += interes + depositoMensual
        val nombreMes = fecha.month.getDisplayName(TextStyle.SHORT, Locale("es", "PE"))
            .replaceFirstChar { it.uppercase() }
        proyeccion.add(FilaProyeccion("$nombreMes ${fecha.year}", depositoMensual, interes, saldo))
    }
    return proyeccion
}

@Composable
fun AhorroScreen(onBack: () -> Unit) {
    val ahorro          = DemoData.metaAhorro
    val tasaInteres     = 3.5   // % anual
    val depositoMensual = 500.0 // S/ mensuales de depósito

    val pct          = ahorro.porcentaje()
    val mesesFaltan  = calcularMesesParaMeta(ahorro.saldo, ahorro.meta, tasaInteres, depositoMensual)
    val proyeccion   = generarProyeccion(ahorro.saldo, tasaInteres, depositoMensual)

    val fmt = NumberFormat.getNumberInstance(Locale("es", "PE")).apply {
        minimumFractionDigits = 2; maximumFractionDigits = 2
    }

    // Emoji motivacional según progreso
    val emojiMotivacional = when {
        pct > 0.75f -> "🔥 ¡Casi lo logras!"
        pct > 0.50f -> "💪 ¡Vas muy bien!"
        pct > 0.25f -> "🌱 ¡Buen comienzo!"
        else        -> "💰 ¡Cada sol cuenta!"
    }

    // Animación del progreso: va de 0 a pct suavemente al entrar a la pantalla
    val animProgress by animateFloatAsState(
        targetValue  = pct,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutCubic),
        label        = "progressAnimation"
    )

    Scaffold(
        topBar = {
            MiBancoTopBar(titulo = "Meta de Ahorro", mostrarBack = true, onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Tarjeta principal con barra animada ────────────────────────
            item {
                Card(
                    modifier  = Modifier.fillMaxWidth(),
                    shape     = RoundedCornerShape(16.dp),
                    colors    = CardDefaults.cardColors(containerColor = NavyPrimary),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier            = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Savings, "Ahorro",
                            tint     = GoldLight,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(ahorro.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "S/ ${fmt.format(ahorro.saldo)}",
                            color      = GoldLight,
                            fontSize   = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            "de S/ ${fmt.format(ahorro.meta)}",
                            color    = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.height(20.dp))
                        // LinearProgressIndicator animado con clip para bordes redondeados
                        // height(14.dp) y clip() según instrucciones del ejercicio
                        LinearProgressIndicator(
                            progress   = { animProgress },
                            modifier   = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .clip(RoundedCornerShape(7.dp)),
                            color      = GreenPositive,
                            trackColor = Color.White.copy(alpha = 0.25f)
                        )
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "${(pct * 100).toInt()}% completado",
                            color      = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(8.dp))
                        // Emoji motivacional
                        Text(
                            emojiMotivacional,
                            color    = GoldLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Resumen + proyección de meses ─────────────────────────────
            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Proyección de tu meta", fontWeight = FontWeight.SemiBold, color = NavyDark)
                        Spacer(Modifier.height(12.dp))
                        FilaDetalle("Saldo actual",    "S/ ${fmt.format(ahorro.saldo)}")
                        FilaDetalle("Meta objetivo",   "S/ ${fmt.format(ahorro.meta)}")
                        FilaDetalle("Te faltan",       "S/ ${fmt.format(ahorro.meta - ahorro.saldo)}")
                        FilaDetalle("Depósito mensual","S/ ${fmt.format(depositoMensual)}")
                        FilaDetalle("Tasa de interés", "$tasaInteres% anual")
                        Spacer(Modifier.height(8.dp))
                        // Cálculo de meses y fecha aproximada
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = GreenPositive.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text     = "🎯 Alcanzarás tu meta en $mesesFaltan meses\n" +
                                        "(aprox. ${mesYAnio(mesesFaltan)})",
                                modifier = Modifier.padding(12.dp),
                                color    = GreenPositive,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // ── Tabla de proyección de 6 meses ────────────────────────────
            item {
                Text(
                    "Proyección próximos 6 meses",
                    fontWeight = FontWeight.SemiBold,
                    color      = NavyDark,
                    fontSize   = 15.sp
                )
            }

            // Encabezado tabla
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
                        AhorroHeader("Mes",       Modifier.weight(1.4f))
                        AhorroHeader("Depósito",  Modifier.weight(1f))
                        AhorroHeader("Interés",   Modifier.weight(1f))
                        AhorroHeader("Saldo",     Modifier.weight(1.2f))
                    }
                }
            }

            // Filas de proyección
            itemsIndexed(proyeccion) { index, fila ->
                Card(
                    shape  = if (index == proyeccion.lastIndex)
                        RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                    else
                        RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(
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
                        AhorroCell(fila.mes,                    Modifier.weight(1.4f))
                        AhorroCell(fmt.format(fila.deposito),   Modifier.weight(1f), color = NavyPrimary)
                        AhorroCell(fmt.format(fila.interes),    Modifier.weight(1f), color = GreenPositive)
                        AhorroCell(fmt.format(fila.saldoProyectado), Modifier.weight(1.2f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Calcula el mes y año aproximado dada la cantidad de meses desde hoy
private fun mesYAnio(meses: Int): String {
    val fecha     = LocalDate.now().plusMonths(meses.toLong())
    val nombreMes = fecha.month.getDisplayName(TextStyle.FULL, Locale("es", "PE"))
        .replaceFirstChar { it.uppercase() }
    return "$nombreMes ${fecha.year}"
}

@Composable
private fun FilaDetalle(etiqueta: String, valor: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = GrayDark, fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 13.sp)
    }
    HorizontalDivider(color = Color(0xFFEEEEEE))
}

@Composable
private fun AhorroHeader(texto: String, modifier: Modifier) {
    Text(texto, modifier = modifier, color = Color.White, fontSize = 11.sp,
        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
}

@Composable
private fun AhorroCell(
    texto: String,
    modifier: Modifier,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = NavyDark
) {
    Text(texto, modifier = modifier, fontSize = 11.sp,
        fontWeight = fontWeight, color = color, textAlign = TextAlign.Center)
}
