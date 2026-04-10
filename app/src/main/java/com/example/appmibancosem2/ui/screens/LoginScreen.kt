package com.example.appmibancosem2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appmibancosem2.ui.theme.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    // remember { mutableStateOf("") } = variable observable que persiste
    // entre recomposiciones. "by" = delegación, acceso directo sin .value
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf("") }
    var verPassword by remember { mutableStateOf(false) }

    // Degradado vertical de fondo: efecto visual premium con una línea
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(NavyDark, NavyPrimary))
            ),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier            = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo / Título
                Text(
                    text       = "Mi",
                    fontSize   = 40.sp,
                    fontWeight = FontWeight.Black,
                    color      = GoldAccent
                )
                Text(
                    text       = "BANCO",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Black,
                    color      = NavyPrimary,
                    letterSpacing = 6.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text     = "Banca Personal",
                    fontSize = 13.sp,
                    color    = GrayMedium
                )
                Spacer(Modifier.height(28.dp))

                // Campo Email
                OutlinedTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = { Text("Correo electrónico") },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(14.dp))

                // Campo Contraseña con toggle de visibilidad
                OutlinedTextField(
                    value         = password,
                    onValueChange = { password = it },
                    label         = { Text("Contraseña") },
                    singleLine    = true,
                    visualTransformation = if (verPassword)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon  = {
                        IconButton(onClick = { verPassword = !verPassword }) {
                            Icon(
                                imageVector = if (verPassword)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/ocultar contraseña",
                                tint = GrayMedium
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp)
                )

                // Mensaje de error (vacío si no hay error)
                if (error.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text     = error,
                        color    = RedNegative,
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.height(20.dp))

                // Botón Ingresar
                Button(
                    onClick = {
                        when {
                            email.isBlank()    -> error = "Ingresa tu correo"
                            password.isBlank() -> error = "Ingresa tu contraseña"
                            password.length < 4 -> error = "Contraseña muy corta"
                            else               -> {
                                error = ""
                                onLoginSuccess() // Navega al Dashboard
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                ) {
                    Text(
                        text       = "Ingresar",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = { }) {
                    Text(
                        text  = "¿Olvidaste tu contraseña?",
                        color = NavyPrimary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
