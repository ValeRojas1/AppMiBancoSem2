package com.example.appmibancosem2.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login")
    object Dashboard     : Screen("dashboard")
    // Transacciones recibe el número de cuenta como argumento de ruta
    object Transacciones : Screen("transacciones/{numeroCuenta}") {
        fun crearRuta(numeroCuenta: String) = "transacciones/$numeroCuenta"
    }
    object Pagos         : Screen("pagos")
    object Prestamos     : Screen("prestamos")
    object Ahorro        : Screen("ahorro")
    object SolicitudCredito : Screen("solicitud_credito")  // ← AGREGAR
}
