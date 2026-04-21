package com.lockin.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockPerson
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel, onNext: () -> Unit) {
    val email by authViewModel.email.collectAsState()
    val fm = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF12121C), Background))
            )
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))

            // Logo area
            Icon(
                imageVector = Icons.Outlined.LockPerson,
                contentDescription = null,
                tint = Indigo500,
                modifier = Modifier.size(64.dp)
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "LockIn",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Your private productivity vault.",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(Modifier.height(52.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { authViewModel.setEmail(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email address") },
                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Indigo500) },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    fm.clearFocus()
                    if (email.isNotBlank()) onNext()
                }),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo500,
                    unfocusedBorderColor = StrokeSubtle,
                    focusedLabelColor = Indigo500,
                    cursorColor = Indigo500,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { fm.clearFocus(); onNext() },
                enabled = email.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo500)
            ) {
                Text(
                    "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(Modifier.weight(1.5f))
        }
    }
}
