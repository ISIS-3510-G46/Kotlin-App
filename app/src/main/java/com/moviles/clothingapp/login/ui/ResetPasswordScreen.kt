package com.moviles.clothingapp.login.ui

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.moviles.clothingapp.login.ResetPasswordViewModel
import com.moviles.clothingapp.ui.utils.DarkGreen
import com.moviles.clothingapp.ui.utils.NetworkHelper.isInternetAvailable
import com.moviles.clothingapp.ui.utils.NoInternetMessage


@Composable
fun ResetPasswordScreen(resetPasswordViewModel: ResetPasswordViewModel = viewModel(), navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val resetPasswordResult by resetPasswordViewModel.resetPasswordResult.observeAsState()
    val trace: Trace = remember { FirebasePerformance.getInstance().newTrace("ResetPassword_Loading") }


    // Efecto popup de link enviado
    LaunchedEffect(resetPasswordResult) {
        resetPasswordResult ?: return@LaunchedEffect

        trace.start()
        when (resetPasswordResult) {
            is ResetPasswordViewModel.ResetPasswordResult.Success -> {
                Toast.makeText(context, "Correo de recuperación enviado.", Toast.LENGTH_SHORT).show()
            }
            is ResetPasswordViewModel.ResetPasswordResult.Failure -> {
                Toast.makeText(context, "Error: El correo ingresado es inválido o no está registrado.", Toast.LENGTH_SHORT).show()
            }
            null -> Log.d("ResetPasswordScreen", "resetPasswordResult is null")
        }
        trace.stop()
    }

    
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    )




    {
        if (!isInternetAvailable(context)) {
            Log.d("Status Internet", isInternetAvailable(context).toString())
            NoInternetMessage(Modifier.height(100.dp))
        }

        TextButton(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Volver", color= DarkGreen)
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkGreen,
                focusedLabelColor = DarkGreen,
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (email.isBlank()) {
                    Toast.makeText(context, "Por favor ingresa tu correo electrónico.", Toast.LENGTH_SHORT).show()
                } else {
                    resetPasswordViewModel.sendPasswordResetEmail(email)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
        ) {
            Text("Restablecer contraseña")
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Ingresa tu correo electronico, enviaremos un link de recuperacion de contrasena.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(bottom = 32.dp)
        )


    }
}

