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

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateTo = { screen -> navController.navigate(screen.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = Screen.Transacciones.route,
            arguments = listOf(navArgument("numeroCuenta") { defaultValue = "" })
        ) { backStackEntry ->
            val numeroCuenta = backStackEntry.arguments?.getString("numeroCuenta") ?: ""
            TransaccionesScreen(
                onBack       = { navController.popBackStack() },
                numeroCuenta = numeroCuenta
            )
        }

        composable(Screen.Pagos.route) {
            PagosScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Prestamos.route) {
            PrestamosScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Ahorro.route) {
            AhorroScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.SolicitudCredito.route) {
            SolicitudCreditoScreen(onBack = { navController.popBackStack() })
        }
    }
}