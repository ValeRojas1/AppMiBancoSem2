package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.Cuenta
import com.example.appmibancosem2.data.model.Transaccion
import com.example.appmibancosem2.ui.theme.*

// ─────────────────────────────────────────────
// TOP BAR — Barra de navegación superior
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiBancoTopBar(
    titulo: String,
    mostrarBack: Boolean = false,  // false = sin flecha de regreso
    onBack: () -> Unit = {}        // lambda vacío por defecto (no hace nada)
) {
    TopAppBar(
        title = {
            Text(
                text       = titulo,
                color      = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            // Solo muestra la flecha si la pantalla lo requiere
            if (mostrarBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint               = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = NavyPrimary
        )
    )
}

// ─────────────────────────────────────────────
// TARJETA CUENTA — Muestra saldo y número de cuenta
// modifier: Modifier = Modifier → patrón estándar en Compose
// Permite al llamador pasar modificadores extra sin que el componente los fuerce
// ─────────────────────────────────────────────

@Composable
fun TarjetaCuenta(cuenta: Cuenta, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(NavyPrimary, NavyLight)
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text       = cuenta.tipo.uppercase(),
                    color      = GoldLight,
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text       = "S/ ${String.format("%,.2f", cuenta.saldo)}",
                    color      = Color.White,
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text  = cuenta.numero,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text  = cuenta.titular,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// BOTÓN ACCESO RÁPIDO — Íconos del Dashboard
// ─────────────────────────────────────────────

@Composable
fun BotonAccesoRapido(
    icono: ImageVector,
    etiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier         = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledTonalButton(
            onClick = onClick,
            modifier = Modifier.size(64.dp),
            shape    = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(0.dp),
            colors   = ButtonDefaults.filledTonalButtonColors(
                containerColor = NavyPrimary.copy(alpha = 0.1f)
            )
        ) {
            Icon(
                imageVector        = icono,
                contentDescription = etiqueta,
                tint               = NavyPrimary,
                modifier           = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text     = etiqueta,
            fontSize = 12.sp,
            color    = GrayDark,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
// FILA TRANSACCIÓN — Cada movimiento en la lista
// ─────────────────────────────────────────────

@Composable
fun FilaTransaccion(transaccion: Transaccion) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Barra delgada de color indicador (rojo = débito, verde = crédito)
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 40.dp)
                .background(
                    color = if (transaccion.esDebito()) RedNegative else GreenPositive,
                    shape = RoundedCornerShape(4.dp)
                )
        )
        Spacer(Modifier.width(12.dp))
        // weight(1f) = ocupa todo el espacio disponible (equivalente a flex:1 en web)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = transaccion.descripcion,
                fontWeight = FontWeight.Medium,
                fontSize   = 14.sp,
                color      = NavyDark
            )
            Text(
                text     = "${transaccion.fecha}  ·  ${transaccion.categoria}",
                fontSize = 12.sp,
                color    = GrayMedium
            )
        }
        Text(
            text  = (if (transaccion.esDebito()) "−" else "+") + transaccion.montoFormateado(),
            color = if (transaccion.esDebito()) RedNegative else GreenPositive,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 14.sp
        )
    }
    HorizontalDivider(color = Color(0xFFEEEEEE))
}
