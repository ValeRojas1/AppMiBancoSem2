package com.example.appmibancosem2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.appmibancosem2.ui.screens.*

@Composable
fun MiBancoNavGraph(navController: NavHostController) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {

        // ── LOGIN ──────────────────────────────────────────────────────────
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        // Elimina Login del back stack: no vuelve al login con "atrás"
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // ── DASHBOARD ─────────────────────────────────────────────────────
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavTransacciones = { numeroCuenta -> 
                    navController.navigate(Screen.Transacciones.crearRuta(numeroCuenta)) 
                },
                onNavPagos         = { navController.navigate(Screen.Pagos.route) },
                onNavPrestamos     = { navController.navigate(Screen.Prestamos.route) },
                onNavAhorro        = { navController.navigate(Screen.Ahorro.route) },
                // TAREA: Logout — limpia TODO el back stack con popUpTo(0)
                onLogout           = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }  // 0 = raíz del back stack, borra todo
                    }
                }
            )
        }

        // ── TRANSACCIONES ─────────────────────────────────────────────────
        // En NavGraph.kt — reemplaza el composable de Transacciones:
        composable(
            route     = Screen.Transacciones.route,
            arguments = listOf(navArgument("numeroCuenta") { defaultValue = "" })
        ) { backStackEntry ->
            // Recuperar el argumento pasado desde Dashboard
            val numeroCuenta = backStackEntry.arguments?.getString("numeroCuenta") ?: ""
            TransaccionesScreen(
                onBack        = { navController.popBackStack() },
                numeroCuenta  = numeroCuenta  // se pasa a la pantalla
            )
        }


        // ── PAGOS ─────────────────────────────────────────────────────────
        composable(Screen.Pagos.route) {
            PagosScreen(onBack = { navController.popBackStack() })
        }

        // ── PRÉSTAMOS ─────────────────────────────────────────────────────
        composable(Screen.Prestamos.route) {
            PrestamosScreen(onBack = { navController.popBackStack() })
        }

        // ── AHORRO ────────────────────────────────────────────────────────
        composable(Screen.Ahorro.route) {
            AhorroScreen(onBack = { navController.popBackStack() })
        }
    }
}
