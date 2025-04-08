package com.moviles.clothingapp.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.moviles.clothingapp.R
import com.moviles.clothingapp.login.LoginViewModel
import com.moviles.clothingapp.ui.utils.DarkGreen
import kotlinx.coroutines.delay

@Composable
fun CreateAccountScreen(loginViewModel: LoginViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val trace: Trace = remember { FirebasePerformance.getInstance().newTrace("CreateAccountScreen_Loading") }

    // Observar si ocurre un error
    val signUpErrorMessage by loginViewModel.signUpErrorMessage.collectAsState()

    LaunchedEffect(Unit) {
        trace.start()
        trace.stop()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(300.dp)
        )


        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                focusedLabelColor = DarkGreen,
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                focusedLabelColor = DarkGreen,
            )
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                focusedLabelColor = DarkGreen,
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Solo si hay error se muestra esto:
        signUpErrorMessage?.let { errorMsg ->
            // Check if the error is related to no network detected
            val errorMessage = if (errorMsg.contains("A Network error", ignoreCase = true)) {
                "Por favor verifica tu conexion a internet para ingresar."
            } else {
                errorMsg // Default error message
            }
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Quitar el error despues de 500ms
            LaunchedEffect(errorMsg) {
                delay(5000)
                loginViewModel.clearSignUpError()
            }
        }

        Button(
            onClick = {
                if (password == confirmPassword) {
                    loginViewModel.signUp(email, password)
                } else {
                    loginViewModel.setPasswordMismatchError()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text("Registrarse", color = Color.White)
        }

        TextButton(
            onClick = {
                // Navigate back to login screen
                navController.popBackStack()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Ya tienes una cuenta", color = Color.Black)
        }
    }

    val navigateToHome by loginViewModel.navigateToHome.collectAsState()
    LaunchedEffect(navigateToHome) {
        if (navigateToHome) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }
}