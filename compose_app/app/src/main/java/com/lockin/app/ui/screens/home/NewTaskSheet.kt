package com.lockin.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lockin.app.model.TaskItem
import com.lockin.app.ui.theme.*
import com.lockin.app.viewmodel.SettingsViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskSheet(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onSave: (TaskItem) -> Unit
) {
    val defaultPrivate by settingsViewModel.defaultPrivateTasks.collectAsState()

    var title by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(defaultPrivate) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis() + 3_600_000) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
    val timePickerState = rememberTimePickerState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "New Task",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What do you need to get done?") },
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo500,
                    unfocusedBorderColor = StrokeSubtle,
                    focusedLabelColor = Indigo500,
                    cursorColor = Indigo500,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = Indigo400
                )
            )

            // Date Picker inline
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceCard,
                    titleContentColor = TextSecondary,
                    headlineContentColor = Color.White,
                    selectedDayContainerColor = Indigo500,
                    todayDateBorderColor = Indigo400
                ),
                title = null,
                headline = null,
                showModeToggle = false
            )

            // Private toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Mark as Private", style = MaterialTheme.typography.titleMedium.copy(color = Color.White))
                    Text("Hide contents in privacy mode", style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary))
                }
                Switch(
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Yellow400
                    )
                )
            }

            Button(
                onClick = {
                    val pickedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val task = TaskItem(
                        id = UUID.randomUUID().toString(),
                        title = title.trim(),
                        dateMillis = pickedDate,
                        isPrivate = isPrivate
                    )
                    if (task.title.isNotEmpty()) onSave(task)
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo500)
            ) {
                Text("Save Task", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            }
        }
    }
}
