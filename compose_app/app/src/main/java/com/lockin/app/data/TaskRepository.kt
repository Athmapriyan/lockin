package com.lockin.app.data

import android.content.Context
import com.lockin.app.model.TaskItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class TaskRepository(private val context: Context) {
    private val file get() = File(context.filesDir, "lockin_vault.json")
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    suspend fun loadTasks(): List<TaskItem> = withContext(Dispatchers.IO) {
        runCatching {
            if (!file.exists()) return@withContext emptyList()
            json.decodeFromString<List<TaskItem>>(file.readText())
        }.getOrElse { emptyList() }
    }

    suspend fun saveTasks(tasks: List<TaskItem>) = withContext(Dispatchers.IO) {
        runCatching { file.writeText(json.encodeToString(tasks)) }
    }

    suspend fun wipeAllData() = withContext(Dispatchers.IO) {
        runCatching { if (file.exists()) file.delete() }
    }
}
