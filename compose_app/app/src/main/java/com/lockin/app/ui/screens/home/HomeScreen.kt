package com.lockin.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.app.model.TaskItem
import com.lockin.app.ui.components.*
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.SettingsViewModel
import com.lockin.app.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(taskViewModel: TaskViewModel, settingsViewModel: SettingsViewModel) {
    val tasks by taskViewModel.tasks.collectAsState()
    val searchQuery by taskViewModel.searchQuery.collectAsState()
    val selectedTab by taskViewModel.selectedTab.collectAsState()
    val privacyMode by settingsViewModel.privacyModeEnabled.collectAsState()
    val hideDetails by settingsViewModel.hideNotifDetails.collectAsState()
    val haptic = LocalHapticFeedback.current

    var showNewTask by remember { mutableStateOf(false) }

    // Focus stats
    val today = remember { Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis }
    val tomorrow = today + 86_400_000L
    val todayTasks = tasks.filter { it.dateMillis in today until tomorrow }
    val totalToday = todayTasks.size
    val completedToday = todayTasks.count { it.completed }
    val focusScore = if (totalToday > 0) ((completedToday.toFloat() / totalToday) * 100).toInt() else 0
    val privatePercent = if (totalToday > 0) ((todayTasks.count { it.isPrivate }.toFloat() / totalToday) * 100).toInt() else 0

    // Filter tasks by current tab + search
    val filteredTasks = tasks.filter { task ->
        val matchesSearch = searchQuery.isEmpty() || task.title.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            0 -> task.dateMillis in today until tomorrow && !task.completed
            1 -> task.dateMillis >= tomorrow && !task.completed
            2 -> task.completed
            else -> true
        }
        matchesSearch && matchesTab
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); showNewTask = true },
                containerColor = Indigo500,
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Outlined.Add, null, tint = Color.White)
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // ── Header ──────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val day = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date()).uppercase()
                        val date = SimpleDateFormat("MMMM d", Locale.getDefault()).format(Date())
                        Text(day, style = MaterialTheme.typography.labelLarge.copy(color = TextTertiary, letterSpacing = 1.5.sp))
                        Text(date, style = MaterialTheme.typography.headlineLarge.copy(color = Color.White, fontWeight = FontWeight.Black))
                    }
                    // Privacy eye toggle
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        settingsViewModel.togglePrivacyMode()
                    }) {
                        Icon(
                            if (privacyMode) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle Privacy",
                            tint = if (privacyMode) Yellow400 else TextSecondary
                        )
                    }
                }
            }

            // ── Focus Score Card ─────────────────────────────────
            item {
                LockInCard(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FocusRing(score = focusScore, size = 120.dp, strokeWidth = 10.dp)
                        Spacer(Modifier.width(24.dp))
                        Column {
                            StatRow(label = "Total Today", value = "$totalToday tasks")
                            Spacer(Modifier.height(8.dp))
                            StatRow(label = "Completed", value = "$completedToday / $totalToday")
                            Spacer(Modifier.height(8.dp))
                            StatRow(label = "Privacy Hub", value = "$privatePercent%", accent = Yellow400)
                        }
                    }
                }
            }

            // ── Search Bar ────────────────────────────────────────
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { taskViewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    placeholder = { Text("Search tasks…", color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TextTertiary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { taskViewModel.setSearchQuery("") }) {
                                Icon(Icons.Outlined.Close, null, tint = TextTertiary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Indigo500,
                        unfocusedBorderColor = StrokeSubtle,
                        cursorColor = Indigo500,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            // ── Tab Selector ─────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .background(SurfaceCard, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Today", "Upcoming", "Completed").forEachIndexed { i, label ->
                        val sel = selectedTab == i
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (sel) Indigo500 else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                taskViewModel.setTab(i)
                            }) {
                                Text(
                                    label,
                                    color = if (sel) Color.White else TextSecondary,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }

            // ── Task List ─────────────────────────────────────────
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyState(tab = selectedTab)
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                            TaskCard(
                                task = task,
                                isPrivacyMode = privacyMode,
                                onToggle = { taskViewModel.toggleCompletion(task.id, privacyMode, hideDetails) },
                                onDelete = { taskViewModel.deleteTask(task.id) },
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNewTask) {
        NewTaskSheet(
            settingsViewModel = settingsViewModel,
            onDismiss = { showNewTask = false },
            onSave = { task ->
                taskViewModel.addTask(task, privacyMode, hideDetails)
                showNewTask = false
            }
        )
    }
}

@Composable
private fun StatRow(label: String, value: String, accent: Color = TextPrimary) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
        Spacer(Modifier.width(8.dp))
        Text(value, style = MaterialTheme.typography.titleMedium.copy(color = accent, fontWeight = FontWeight.Bold))
    }
}

@Composable
private fun EmptyState(tab: Int) {
    val (icon, title, sub) = when (tab) {
        0 -> Triple(Icons.Outlined.CheckCircle, "All Caught Up!", "No tasks scheduled for today.")
        1 -> Triple(Icons.Outlined.CalendarToday, "Nothing Upcoming", "Enjoy the clear road ahead.")
        else -> Triple(Icons.Outlined.Done, "No Completions Yet", "Finish some tasks to see them here.")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = TextTertiary, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge.copy(color = TextSecondary))
        Spacer(Modifier.height(6.dp))
        Text(sub, style = MaterialTheme.typography.bodyMedium.copy(color = TextTertiary))
    }
}
