package com.example.appmibancosem2.data.model

import java.util.Date

data class SolicitudCredito(
    val id: Int = 0,
    val monto: Double,
    val plazoMeses: Int,
    val tipoCredito: String,
    val dniSolicitante: String,
    val estado: String = "pendiente", // pendiente, enviada, rechazada
    val fecha: Long = System.currentTimeMillis()
)
