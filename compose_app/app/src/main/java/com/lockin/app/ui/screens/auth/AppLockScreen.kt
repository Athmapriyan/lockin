package com.lockin.app.ui.screens.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.lockin.app.ui.components.NumericKeypad
import com.lockin.app.ui.components.PinDots
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.AuthViewModel
import com.lockin.app.viewmodel.SettingsViewModel

@Composable
fun AppLockScreen(
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val faceIdEnabled by settingsViewModel.faceIdEnabled.collectAsState()
    val pinError by authViewModel.pinError.collectAsState()

    var pin by remember { mutableStateOf("") }

    // Try biometrics on first composition
    LaunchedEffect(faceIdEnabled) {
        if (faceIdEnabled) launchBiometrics(context, authViewModel)
    }

    // Auto-submit when 4 digits entered
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            val ok = authViewModel.submitPin(pin)
            if (!ok) pin = ""
        }
    }

    // Reset error after brief display
    LaunchedEffect(pinError) {
        if (pinError) {
            kotlinx.coroutines.delay(800)
            authViewModel.resetPinError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0A14), Background))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.weight(1f))

            Icon(Icons.Outlined.Lock, null, tint = Indigo500, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Enter Passcode",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black, color = Color.White
                )
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "LockIn Vault is secured",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )

            Spacer(Modifier.height(40.dp))

            PinDots(pinLength = pin.length, hasError = pinError)

            AnimatedVisibility(visible = pinError, enter = fadeIn()) {
                Spacer(Modifier.height(12.dp))
                Text("Incorrect passcode", color = Danger, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(52.dp))

            NumericKeypad(
                onKey = { if (pin.length < 4) pin += it },
                onBackspace = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
            )

            if (faceIdEnabled) {
                Spacer(Modifier.height(28.dp))
                IconButton(onClick = { launchBiometrics(context, authViewModel) }) {
                    Icon(
                        Icons.Outlined.Fingerprint,
                        contentDescription = "Use Biometrics",
                        tint = Indigo400,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Text("Use Face / Fingerprint", style = MaterialTheme.typography.labelLarge.copy(color = Indigo400))
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

private fun launchBiometrics(context: android.content.Context, authViewModel: AuthViewModel) {
    val activity = context as? FragmentActivity ?: return
    val biometricManager = BiometricManager.from(context)
    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        != BiometricManager.BIOMETRIC_SUCCESS) return

    val prompt = BiometricPrompt(
        activity,
        ContextCompat.getMainExecutor(activity),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                authViewModel.unlockViaBiometrics()
            }
        }
    )
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("LockIn Access")
        .setSubtitle("Confirm your identity to open your vault")
        .setNegativeButtonText("Use PIN")
        .build()
    prompt.authenticate(info)
}
