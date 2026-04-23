package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.data.local.SolicitudDbHelper
import com.example.appmibancosem2.data.model.SolicitudCredito
import com.example.appmibancosem2.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistorialSolicitudesScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val dbHelper = remember { SolicitudDbHelper(context) }
    
    var solicitudes by remember { mutableStateOf(listOf<SolicitudCredito>()) }
    var pendientesCount by remember { mutableStateOf(0) }
    var showDeleteDialog by remember { mutableStateOf<Int?>(null) }

    fun cargarDatos() {
        solicitudes = dbHelper.obtenerTodas()
        pendientesCount = dbHelper.contarPendientes()
    }

    LaunchedEffect(Unit) {
        cargarDatos()
    }

    Scaffold(
        topBar = {
            MiBancoTopBar(
                titulo = "Historial de Solicitudes",
                mostrarBack = true,
                onBack = onBack
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Banner superior
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Guardadas", color = Color.White, fontSize = 14.sp)
                        Text("${solicitudes.size}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    if (pendientesCount > 0) {
                        Surface(
                            color = GoldAccent,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "$pendientesCount Pendientes",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                color = NavyDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (solicitudes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.History, null, modifier = Modifier.size(64.dp), tint = GrayMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Aún no hay registros", color = GrayMedium)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(solicitudes) { sol ->
                        TarjetaSolicitud(
                            solicitud = sol,
                            onEliminar = { showDeleteDialog = sol.id },
                            onEnviar = {
                                dbHelper.actualizarEstado(sol.id, "enviada")
                                cargarDatos()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Eliminar Solicitud") },
            text = { Text("¿Estás seguro de que deseas eliminar esta solicitud?") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog?.let { dbHelper.eliminarSolicitud(it) }
                    showDeleteDialog = null
                    cargarDatos()
                }) {
                    Text("Eliminar", color = RedNegative)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TarjetaSolicitud(
    solicitud: SolicitudCredito,
    onEliminar: () -> Unit,
    onEnviar: () -> Unit
) {
    val colorEstado = when (solicitud.estado) {
        "pendiente" -> GoldAccent
        "enviada" -> Color(0xFF4CAF50) // Verde
        "rechazada" -> RedNegative
        else -> GrayMedium
    }

    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val fechaStr = sdf.format(Date(solicitud.fecha))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Barra lateral de color
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorEstado)
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "S/ ${String.format("%.2f", solicitud.monto)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )
                    Text(
                        text = solicitud.estado.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorEstado
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text("DNI: ${solicitud.dniSolicitante}", fontSize = 13.sp)
                Text("Tipo: ${solicitud.tipoCredito} | Plazo: ${solicitud.plazoMeses} meses", fontSize = 13.sp)
                Text("Fecha: $fechaStr", fontSize = 11.sp, color = GrayMedium)

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (solicitud.estado == "pendiente") {
                        IconButton(onClick = onEnviar) {
                            Icon(Icons.Default.Send, "Marcar como enviada", tint = NavyPrimary)
                        }
                    }
                    IconButton(onClick = onEliminar) {
                        Icon(Icons.Default.Delete, "Eliminar", tint = RedNegative)
                    }
                }
            }
        }
    }
}
