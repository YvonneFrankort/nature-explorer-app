package com.example.naturegame.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.naturegame.data.remote.firebase.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Eco

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    authManager: AuthManager = AuthManager()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3FBF5))
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(40.dp))

            Icon(
                imageVector = Icons.Filled.Eco,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Welcome to NatureGame",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF2E7D32)
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
            }

            Button(
                onClick = {
                    loading = true
                    error = null

                    CoroutineScope(Dispatchers.Main).launch {
                        val result = authManager.login(email, password)
                        loading = false

                        if (result.isSuccess) onLoginSuccess()
                        else error = result.exceptionOrNull()?.message
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Login")
                }
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = {
                    if (email.isBlank()) {
                        error = "Enter your email first"
                        return@TextButton
                    }

                    loading = true
                    error = null

                    CoroutineScope(Dispatchers.Main).launch {
                        val result = authManager.sendPasswordReset(email)
                        loading = false

                        if (result.isSuccess) {
                            error = "Password reset email sent"
                        } else {
                            error = result.exceptionOrNull()?.message ?: "Failed to send reset email"
                        }
                    }
                }
            ) {
                Text("Forgot password?")
            }


            TextButton(onClick = onNavigateToRegister) {
                Text("Create an account")
            }
        }
    }
}
