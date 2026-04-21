package com.lockin.app.ui.screens.auth

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.AuthViewModel

@Composable
fun OtpScreen(authViewModel: AuthViewModel, onSuccess: () -> Unit) {
    var otp by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val email by authViewModel.email.collectAsState()

    LaunchedEffect(otp) {
        error = false
        if (otp.length == 6) {
            if (authViewModel.submitOtp(otp)) onSuccess()
            else { error = true; otp = "" }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF12121C), Background)))
            .padding(horizontal = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.weight(1f))

            Text(
                "Verify your email",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black, color = Color.White
                )
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "A 6-digit code was sent to\n$email",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary, textAlign = TextAlign.Center
                )
            )

            Spacer(Modifier.height(44.dp))

            // OTP digit boxes
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)
                    Box(
                        modifier = Modifier
                            .size(52.dp, 64.dp)
                            .border(
                                2.dp,
                                if (index == otp.length) Indigo500 else StrokeSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .background(SurfaceCard, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (char != null) "•" else "",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (error) {
                Spacer(Modifier.height(12.dp))
                Text("Invalid code. Please try again.", color = Danger, fontSize = 13.sp)
            }

            Spacer(Modifier.height(32.dp))

            // Hidden num-pad
            com.lockin.app.ui.components.NumericKeypad(
                onKey = { if (otp.length < 6) otp += it },
                onBackspace = { if (otp.isNotEmpty()) otp = otp.dropLast(1) }
            )

            Spacer(Modifier.weight(1f))
        }
    }
}
