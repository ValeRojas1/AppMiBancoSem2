package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.model.DemoData
import com.example.appmibancosem2.ui.theme.*
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────────────────────
// EJERCICIO 3 — PagosScreen con validación completa
// Estado reactivo: cada campo tiene su propia validación en tiempo real
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosScreen(onBack: () -> Unit) {
    // Estado del formulario
    var servicio          by remember { mutableStateOf("") }
    var contrato          by remember { mutableStateOf("") }
    var monto             by remember { mutableStateOf("") }
    var mostrarErrores    by remember { mutableStateOf(false) }
    var expandido         by remember { mutableStateOf(false) }
    var mostrarModal      by remember { mutableStateOf(false) }
    var pagoRealizado     by remember { mutableStateOf(false) }

    // Validaciones individuales — se recalculan en cada recomposición
    val servicioValido = servicio.isNotEmpty()
    val contratoValido = contrato.length >= 6
    val montoValido    = monto.toDoubleOrNull()?.let { it > 0 } ?: false

    // El formulario es válido solo si TODOS los campos son válidos
    val formularioValido = servicioValido && contratoValido && montoValido

    // SnackBarHostState para el BONUS — SnackBar de confirmación
    val snackBarState  = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo      = "Pago de Servicios",
                mostrarBack = true,
                onBack      = onBack
            )
        },
        // BONUS: SnackBarHost para mostrar mensajes flotantes
        snackbarHost = { SnackbarHost(hostState = snackBarState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "Completa todos los campos para continuar",
                fontSize = 14.sp,
                color    = GrayMedium
            )

            // ── Dropdown de servicios ──────────────────────────────────────
            ExposedDropdownMenuBox(
                expanded         = expandido,
                onExpandedChange = { expandido = !expandido }
            ) {
                OutlinedTextField(
                    value         = if (servicio.isEmpty()) "" else servicio,
                    onValueChange = {},
                    readOnly      = true,
                    label         = { Text("Servicio a pagar *") },
                    placeholder   = { Text("Selecciona un servicio") },
                    trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    // isError: borde rojo si mostrarErrores y el campo es inválido
                    isError       = mostrarErrores && !servicioValido,
                    supportingText = {
                        if (mostrarErrores && !servicioValido)
                            Text("Selecciona un servicio", color = RedNegative, fontSize = 12.sp)
                    },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape         = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded         = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    DemoData.servicios.forEach { srv ->
                        DropdownMenuItem(
                            text    = { Text(srv.nombre) },
                            onClick = {
                                servicio = srv.nombre
                                monto    = srv.montoPredeterminado.toString()
                                expandido = false
                                // BONUS: mostrar SnackBar cuando se selecciona servicio
                                if (formularioValido) {
                                    coroutineScope.launch {
                                        snackBarState.showSnackbar("✅ Listo para confirmar")
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // ── Campo número de contrato ───────────────────────────────────
            OutlinedTextField(
                value         = contrato,
                onValueChange = {
                    contrato = it
                    pagoRealizado = false
                },
                label         = { Text("Número de contrato *") },
                placeholder   = { Text("Mínimo 6 caracteres") },
                singleLine    = true,
                // isError: borde rojo si mostrarErrores y contrato inválido
                isError       = mostrarErrores && !contratoValido,
                supportingText = {
                    if (mostrarErrores && !contratoValido)
                        Text(
                            "El contrato debe tener al menos 6 caracteres",
                            color    = RedNegative,
                            fontSize = 12.sp
                        )
                    else
                        Text("${contrato.length}/6 caracteres mínimo", color = GrayMedium, fontSize = 12.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            )

            // ── Campo monto ───────────────────────────────────────────────
            OutlinedTextField(
                value         = monto,
                onValueChange = {
                    monto = it
                    pagoRealizado = false
                },
                label           = { Text("Monto a pagar *") },
                placeholder     = { Text("0.00") },
                singleLine      = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                prefix          = { Text("S/ ") },
                // isError: borde rojo si monto no es número positivo
                isError         = mostrarErrores && !montoValido,
                supportingText  = {
                    if (mostrarErrores && !montoValido)
                        Text("Ingresa un monto válido mayor a 0", color = RedNegative, fontSize = 12.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp)
            )

            // ── Card de resumen (aparece solo cuando el formulario es válido)
            // El patrón if(condicion) { Composable() } es la forma idiomática en Compose
            if (formularioValido) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = NavyPrimary.copy(alpha = 0.06f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Resumen del pago",
                            fontWeight = FontWeight.SemiBold,
                            color      = NavyDark,
                            fontSize   = 14.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        FilaResumen("Servicio:",  servicio)
                        FilaResumen("Contrato:",  contrato)
                        FilaResumen("Monto:",    "S/ ${"%.2f".format(monto.toDoubleOrNull() ?: 0.0)}")
                    }
                }
            }

            // ── Botón Pagar ────────────────────────────────────────────────
            // enabled = formularioValido: se deshabilita si el formulario no es válido
            Button(
                onClick = {
                    mostrarErrores = true
                    if (formularioValido) {
                        mostrarModal = true
                    }
                },
                enabled  = formularioValido,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    // Color dinámico: azul si válido, gris si no
                    containerColor = if (formularioValido) NavyPrimary else Color.Gray
                )
            ) {
                Text("Confirmar Pago", fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Banner de pago exitoso
            if (pagoRealizado) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = GreenPositive.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text       = "✅  Pago realizado con éxito",
                        modifier   = Modifier.padding(16.dp),
                        color      = GreenPositive,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // AlertDialog de confirmación
    if (mostrarModal) {
        AlertDialog(
            onDismissRequest = { mostrarModal = false },
            title = { Text("Confirmar pago", fontWeight = FontWeight.Bold) },
            text  = {
                Text(
                    "¿Confirmas el pago de S/ ${"%.2f".format(monto.toDoubleOrNull() ?: 0.0)} " +
                            "a $servicio?\nContrato: $contrato"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarModal  = false
                        pagoRealizado = true
                        // BONUS: SnackBar de confirmación
                        coroutineScope.launch {
                            snackBarState.showSnackbar("✅ Listo para confirmar")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) { Text("Confirmar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { mostrarModal = false }) {
                    Text("Cancelar", color = NavyPrimary)
                }
            }
        )
    }
}

// Componente auxiliar privado para el resumen
@Composable
private fun FilaResumen(etiqueta: String, valor: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = GrayDark, fontSize = 13.sp)
        Text(valor, fontWeight = FontWeight.SemiBold, color = NavyDark, fontSize = 13.sp)
    }
}
