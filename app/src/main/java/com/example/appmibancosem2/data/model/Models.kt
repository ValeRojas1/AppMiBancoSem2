package com.example.appmibancosem2.data.model

import java.text.NumberFormat
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// DATA CLASSES — Solo almacenan datos (sin lógica de negocio compleja)
// ─────────────────────────────────────────────────────────────────────────────

data class Cuenta(
    val numero: String,
    val tipo: String,
    val saldo: Double,
    val titular: String
)

data class Transaccion(
    val descripcion: String,
    val fecha: String,
    val monto: Double,          // negativo = débito, positivo = crédito
    val categoria: String = ""  // parámetro opcional con valor por defecto
) {
    fun esDebito() = monto < 0

    fun montoFormateado(): String {
        val fmt = NumberFormat.getNumberInstance(Locale("es", "PE"))
        fmt.minimumFractionDigits = 2
        fmt.maximumFractionDigits = 2
        return "S/ ${fmt.format(kotlin.math.abs(monto))}"
    }
}

// Ejercicio 2 — tipo explícito "debito" / "credito" como String
data class Movimiento(
    val descripcion: String,
    val monto: Double,
    val tipo: String  // "debito" o "credito"
)

data class Servicio(
    val nombre: String,
    val montoPredeterminado: Double
)

data class MetaAhorro(
    val nombre: String,
    val meta: Double,
    val saldo: Double
) {
    fun porcentaje() = (saldo / meta).coerceIn(0.0, 1.0).toFloat()

    fun montoFormateado(): String {
        val fmt = NumberFormat.getNumberInstance(Locale("es", "PE"))
        fmt.minimumFractionDigits = 2
        return "S/ ${fmt.format(saldo)} / S/ ${fmt.format(meta)}"
    }
}

data class SimuladorPrestamo(
    val monto: Double,
    val tasaAnual: Double,
    val cuotas: Int
) {
    fun calcularCuota(): Double {
        val r = tasaAnual / 12.0 / 100.0
        if (r == 0.0) return monto / cuotas
        val factor = Math.pow(1 + r, cuotas.toDouble())
        return monto * (r * factor) / (factor - 1)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DEMO DATA — Singleton con todos los datos de prueba de la app
// object = una sola instancia en toda la app (Singleton)
// ─────────────────────────────────────────────────────────────────────────────

object DemoData {

    // Cuenta principal (Ahorros)
    val cuenta = Cuenta(
        numero  = "0011-0192-0100234567",
        tipo    = "Cuenta de Ahorros MiBanco",
        saldo   = 4_250.75,
        titular = "Vale Rojas"
    )

    // Segunda cuenta para el Dashboard (Tarea — mínimo 2 TarjetaCuenta)
    val cuentaCorriente = Cuenta(
        numero  = "0011-0192-0200198432",
        tipo    = "Cuenta Corriente MiBanco",
        saldo   = 1_875.30,
        titular = "Vale Rojas"
    )

    // Lista principal de transacciones (Transaccion con monto positivo/negativo)
    val transacciones = listOf(
        Transaccion("Supermercado Wong",       "10/04/2026", -145.60, "Compras"),
        Transaccion("Transferencia recibida",  "09/04/2026",  1200.00, "Ingresos"),
        Transaccion("Netflix",                 "08/04/2026",   -44.90, "Entretenimiento"),
        Transaccion("Farmacia Inkafarma",      "07/04/2026",   -38.50, "Salud"),
        Transaccion("Pago Luz (Enel)",         "06/04/2026",  -120.00, "Servicios"),
        Transaccion("Depósito sueldo Abril",   "05/04/2026",  2800.00, "Ingresos"),
        Transaccion("Restaurante Central",     "04/04/2026",   -65.00, "Alimentación"),
        Transaccion("Uber",                    "03/04/2026",   -25.50, "Transporte"),
        Transaccion("Pago Internet (Claro)",   "02/04/2026",   -99.00, "Servicios"),
        Transaccion("Retiro cajero BCP",       "01/04/2026",  -200.00, "Efectivo"),
    )

    // Lista de movimientos para Ejercicio 2 (usa tipo String en vez de monto negativo)
    val movimientos = listOf(
        Movimiento("Pago Agua SEDAPAL",       85.00,   "debito"),
        Movimiento("Depósito Sueldo",       3500.00,   "credito"),
        Movimiento("Netflix Cable",           49.90,   "debito"),
        Movimiento("Transferencia recibida", 500.00,   "credito"),
        Movimiento("Supermercado Wong",      185.60,   "debito"),
        Movimiento("Interés mensual",         37.25,   "credito"),
        Movimiento("Pago Internet Claro",     99.00,   "debito"),
        Movimiento("Transferencia enviada",  250.00,   "debito"),
        Movimiento("Devolución compra",       45.00,   "credito"),
        Movimiento("Pago de luz Enel",       120.00,   "debito"),
    )

    // Servicios para el módulo de Pagos
    val servicios = listOf(
        Servicio("Luz (Enel)",       120.00),
        Servicio("Agua (SEDAPAL)",    65.00),
        Servicio("Internet (Claro)",  99.00),
        Servicio("Gas (Cálidda)",     45.00),
        Servicio("Cable TV",          89.90),
    )

    // Meta de ahorro para AhorroScreen
    val metaAhorro = MetaAhorro(
        nombre = "Vacaciones 2026",
        meta   = 3_000.00,
        saldo  = 1_250.00
    )

}  // ← IMPORTANTE: el objeto DemoData DEBE cerrarse aquí con esta llave
