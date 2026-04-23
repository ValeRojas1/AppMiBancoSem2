package com.example.appmibancosem2.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.local.SolicitudDatabase
import com.example.appmibancosem2.data.model.SolicitudCredito
import com.example.appmibancosem2.ui.theme.*

private const val PREFS_SOLICITUD = "borrador_solicitud"
private const val KEY_MONTO       = "sol_monto"
private const val KEY_PLAZO       = "sol_plazo"
private const val KEY_TIPO        = "sol_tipo"
private const val KEY_DNI         = "sol_dni"

@Composable
fun SolicitudCreditoScreen(
    onBack: () -> Unit,
    onNavigateToHistorial: () -> Unit
) {
    val contexto = LocalContext.current

    var monto by remember { mutableStateOf("") }
    var plazo by remember { mutableStateOf("") }
    var tipo  by remember { mutableStateOf("") }
    var dni   by remember { mutableStateOf("") }

    var mostrarDialogoEnvio      by remember { mutableStateOf(false) }
    var mostrarDialogoBorrador   by remember { mutableStateOf(false) }
    var mensajeError             by remember { mutableStateOf("") }

    // Cargar borrador al abrir la pantalla (solo una vez)
    LaunchedEffect(Unit) {
        val prefs = contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
        monto = prefs.getString(KEY_MONTO, "") ?: ""
        plazo = prefs.getString(KEY_PLAZO, "") ?: ""
        tipo  = prefs.getString(KEY_TIPO,  "") ?: ""
        dni   = prefs.getString(KEY_DNI,   "") ?: ""
    }

    fun guardar(clave: String, valor: String) {
        contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
            .edit().putString(clave, valor).apply()
    }

    fun limpiarBorrador() {
        contexto.getSharedPreferences(PREFS_SOLICITUD, Context.MODE_PRIVATE)
            .edit().clear().apply()
        monto = ""; plazo = ""; tipo = ""; dni = ""
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo      = "Solicitud de Crédito",
                mostrarBack = true,
                onBack      = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text     = "Los campos se guardan automáticamente",
                fontSize = 12.sp,
                color    = GoldAccent
            )

            CampoSolicitud(
                label    = "Monto solicitado (S/)",
                valor    = monto,
                hint     = "Ej: 15000",
                teclado  = KeyboardType.Number,
                icono    = { Icon(Icons.Default.AttachMoney, null, tint = NavyPrimary) },
                onCambia = { monto = it; guardar(KEY_MONTO, it) }
            )
            CampoSolicitud(
                label    = "Plazo (meses)",
                valor    = plazo,
                hint     = "Ej: 24",
                teclado  = KeyboardType.Number,
                icono    = { Icon(Icons.Default.CalendarMonth, null, tint = NavyPrimary) },
                onCambia = { plazo = it; guardar(KEY_PLAZO, it) }
            )
            CampoSolicitud(
                label    = "Tipo de crédito",
                valor    = tipo,
                hint     = "Personal / Hipotecario / Vehicular",
                teclado  = KeyboardType.Text,
                icono    = { Icon(Icons.Default.AccountBalance, null, tint = NavyPrimary) },
                onCambia = { tipo = it; guardar(KEY_TIPO, it) }
            )
            CampoSolicitud(
                label    = "DNI del solicitante",
                valor    = dni,
                hint     = "12345678",
                teclado  = KeyboardType.Number,
                icono    = { Icon(Icons.Default.Badge, null, tint = NavyPrimary) },
                onCambia = { if (it.length <= 8) { dni = it; guardar(KEY_DNI, it) } }
            )

            if (mensajeError.isNotEmpty()) {
                Text(text = mensajeError, color = RedNegative, fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    if (monto.isEmpty() || plazo.isEmpty() || tipo.isEmpty() || dni.isEmpty()) {
                        mensajeError = "Completa todos los campos para enviar."
                    } else {
                        mensajeError = ""
                        mostrarDialogoEnvio = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Text("Enviar Solicitud", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = onNavigateToHistorial,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Text("Ver Historial de Solicitudes", color = NavyPrimary)
            }

            TextButton(
                onClick  = { mostrarDialogoBorrador = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpiar borrador", color = GrayMedium)
            }
        }
    }

    // ── Diálogo: envío exitoso ────────────────────────────────────────────
    if (mostrarDialogoEnvio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEnvio = false },
            title = { Text("Solicitud enviada", fontWeight = FontWeight.Bold) },
            text  = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Tu solicitud ha sido registrada con estado PENDIENTE.")
                    Spacer(Modifier.height(4.dp))
                    Text("Monto: S/ $monto")
                    Text("Plazo: $plazo meses")
                    Text("Tipo: $tipo")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // 1. Guardar en archivo de texto (LogManager)
                        LogManager.registrar(
                            contexto,
                            "Monto: S/ $monto | Plazo: $plazo meses | Tipo: $tipo | DNI: $dni"
                        )
                        
                        // 2. Guardar en Base de Datos Local
                        val sqlDb = SolicitudDatabase(contexto)
                        sqlDb.insertar(
                            SolicitudCredito(
                                monto      = monto.toDoubleOrNull() ?: 0.0,
                                plazoMeses = plazo.toIntOrNull()    ?: 0,
                                tipoCredito = tipo,
                                dniSolicitante = dni,
                                estado     = "pendiente"
                            )
                        )

                        limpiarBorrador()
                        mostrarDialogoEnvio = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) { Text("Aceptar") }
            }
        )
    }

    // ── Diálogo: confirmar limpiar borrador ───────────────────────────────
    if (mostrarDialogoBorrador) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoBorrador = false },
            title = { Text("Limpiar borrador", fontWeight = FontWeight.Bold) },
            text  = { Text("¿Borrar los datos del formulario?") },
            confirmButton = {
                Button(
                    onClick = { limpiarBorrador(); mostrarDialogoBorrador = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = RedNegative)
                ) { Text("Sí") }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoBorrador = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun CampoSolicitud(
    label    : String,
    valor    : String,
    hint     : String,
    teclado  : KeyboardType,
    icono    : @Composable () -> Unit,
    onCambia : (String) -> Unit
) {
    OutlinedTextField(
        value           = valor,
        onValueChange   = onCambia,
        label           = { Text(label) },
        placeholder     = { Text(hint, color = GrayMedium) },
        leadingIcon     = icono,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine      = true,
        modifier        = Modifier.fillMaxWidth()
    )
}
