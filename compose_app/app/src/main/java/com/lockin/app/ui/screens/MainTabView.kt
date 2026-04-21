package com.lockin.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.lockin.app.ui.screens.home.HomeScreen
import com.lockin.app.ui.screens.privacy.PrivacyDashboardScreen
import com.lockin.app.ui.screens.settings.SettingsScreen
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.AuthViewModel
import com.lockin.app.viewmodel.SettingsViewModel
import com.lockin.app.viewmodel.TaskViewModel

data class TabItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val tabs = listOf(
    TabItem("Home",    Icons.Filled.Home,         Icons.Outlined.Home),
    TabItem("Privacy", Icons.Filled.VisibilityOff, Icons.Outlined.VisibilityOff),
    TabItem("Settings",Icons.Filled.Settings,      Icons.Outlined.Settings)
)

@Composable
fun MainTabView(
    taskViewModel: TaskViewModel,
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel
) {
    var selectedTab by remember { mutableStateOf(0) }
    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        // Content
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(tween(220)) togetherWith fadeOut(tween(180))
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 76.dp),
            label = "tab_content"
        ) { tab ->
            when (tab) {
                0 -> HomeScreen(taskViewModel, settingsViewModel)
                1 -> PrivacyDashboardScreen(settingsViewModel, taskViewModel)
                2 -> SettingsScreen(settingsViewModel, authViewModel)
            }
        }

        // Bottom bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .background(SurfaceCard, RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEachIndexed { index, item ->
                    val selected = selectedTab == index

                    AnimatedNavItem(
                        item = item,
                        selected = selected,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedTab = index
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.AnimatedNavItem(
    item: TabItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        if (selected) Indigo500.copy(alpha = 0.15f) else Color.Transparent,
        tween(300),
        label = "nav_bg_${item.label}"
    )
    val iconTint by animateColorAsState(
        if (selected) Indigo500 else TextTertiary,
        tween(300),
        label = "nav_tint_${item.label}"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .background(containerColor, RoundedCornerShape(16.dp))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        AnimatedVisibility(visible = selected) {
            Text(
                item.label,
                style = MaterialTheme.typography.labelSmall.copy(color = iconTint)
            )
        }
    }
}
