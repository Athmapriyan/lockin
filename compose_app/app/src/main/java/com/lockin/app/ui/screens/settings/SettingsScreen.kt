package com.lockin.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lockin.app.ui.components.LockInCard
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.AuthViewModel
import com.lockin.app.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel
) {
    val faceId by settingsViewModel.faceIdEnabled.collectAsState()
    val autoLock by settingsViewModel.autoLockMinutes.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black, color = Color.White)
        )

        Text("Security", style = MaterialTheme.typography.labelLarge.copy(color = TextTertiary, letterSpacing = 1.sp))

        LockInCard(modifier = Modifier.fillMaxWidth()) {
            SettingsToggle(
                icon = Icons.Outlined.Fingerprint,
                title = "Face ID / Biometrics",
                subtitle = "Unlock vault with biometrics",
                checked = faceId,
                onToggle = { settingsViewModel.toggleFaceId() }
            )

            HorizontalDivider(color = StrokeSubtle, modifier = Modifier.padding(vertical = 12.dp))

            // Auto lock timer
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Timer, null, tint = Indigo400, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Auto Lock", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                    Text("Lock after $autoLock min of inactivity", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                }
            }
            Slider(
                value = autoLock.toFloat(),
                onValueChange = { settingsViewModel.setAutoLock(it.toInt()) },
                valueRange = 1f..30f,
                steps = 4,
                colors = SliderDefaults.colors(
                    thumbColor = Indigo500,
                    activeTrackColor = Indigo500
                )
            )
        }

        Text("Account", style = MaterialTheme.typography.labelLarge.copy(color = TextTertiary, letterSpacing = 1.sp))

        LockInCard(modifier = Modifier.fillMaxWidth(), onClick = { showLogoutDialog = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Danger.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Logout, null, tint = Danger, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Log Out", style = MaterialTheme.typography.titleMedium.copy(color = Danger, fontWeight = FontWeight.SemiBold))
                    Text("Sign out and lock the vault", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out?", color = Color.White) },
            text = { Text("You will need to re-authenticate to access your tasks.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { authViewModel.fullLogout(); showLogoutDialog = false }) {
                    Text("Log Out", color = Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun SettingsToggle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = if (checked) Indigo400 else TextTertiary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
            Text(subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        }
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Indigo500
            )
        )
    }
}
