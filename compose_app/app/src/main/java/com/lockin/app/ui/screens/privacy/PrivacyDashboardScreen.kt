package com.lockin.app.ui.screens.privacy

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
import com.lockin.app.viewmodel.SettingsViewModel
import com.lockin.app.viewmodel.TaskViewModel

@Composable
fun PrivacyDashboardScreen(
    settingsViewModel: SettingsViewModel,
    taskViewModel: TaskViewModel
) {
    val privacyMode by settingsViewModel.privacyModeEnabled.collectAsState()
    val hideNotif by settingsViewModel.hideNotifDetails.collectAsState()
    val defaultPrivate by settingsViewModel.defaultPrivateTasks.collectAsState()
    val tasks by taskViewModel.tasks.collectAsState()

    val totalPrivate = tasks.count { it.isPrivate }

    var showWipeDialog by remember { mutableStateOf(false) }

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
            "Privacy Dashboard",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Black, color = Color.White
            )
        )

        // Privacy Hub banner
        LockInCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Yellow400.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.VisibilityOff, null, tint = Yellow400, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        "$totalPrivate Private Tasks",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        "Shielded from prying eyes",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Toggles
        Text("Settings", style = MaterialTheme.typography.labelLarge.copy(color = TextTertiary, letterSpacing = 1.sp))

        LockInCard(modifier = Modifier.fillMaxWidth()) {
            PrivacyToggleRow(
                icon = Icons.Outlined.Visibility,
                title = "Privacy Mode",
                subtitle = "Mask all private tasks from view",
                checked = privacyMode,
                onToggle = { settingsViewModel.togglePrivacyMode() }
            )
            HorizontalDivider(color = StrokeSubtle, modifier = Modifier.padding(vertical = 12.dp))
            PrivacyToggleRow(
                icon = Icons.Outlined.NotificationsOff,
                title = "Hide Notification Details",
                subtitle = "Replace private task names in alerts",
                checked = hideNotif,
                onToggle = { settingsViewModel.toggleHideNotifDetails() }
            )
            HorizontalDivider(color = StrokeSubtle, modifier = Modifier.padding(vertical = 12.dp))
            PrivacyToggleRow(
                icon = Icons.Outlined.Lock,
                title = "Default Private Tasks",
                subtitle = "New tasks are private by default",
                checked = defaultPrivate,
                onToggle = { settingsViewModel.toggleDefaultPrivate() }
            )
        }

        Text("Data", style = MaterialTheme.typography.labelLarge.copy(color = TextTertiary, letterSpacing = 1.sp))

        // Wipe all data
        LockInCard(modifier = Modifier.fillMaxWidth(), onClick = { showWipeDialog = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Danger.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.DeleteForever, null, tint = Danger, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text("Wipe All Data", style = MaterialTheme.typography.titleMedium.copy(color = Danger, fontWeight = FontWeight.SemiBold))
                    Text("Permanently clear the vault", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                }
            }
        }

        Spacer(Modifier.height(80.dp))
    }

    if (showWipeDialog) {
        AlertDialog(
            onDismissRequest = { showWipeDialog = false },
            title = { Text("Wipe All Data?", color = Color.White) },
            text = { Text("This will permanently delete all tasks and cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { taskViewModel.wipeAllData(); showWipeDialog = false }) {
                    Text("Wipe", color = Danger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWipeDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun PrivacyToggleRow(
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
