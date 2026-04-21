package com.lockin.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lockin.app.data.TaskRepository
import com.lockin.app.data.cancelNotification
import com.lockin.app.data.scheduleNotification
import com.lockin.app.model.TaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = TaskRepository(app)

    private val _tasks = MutableStateFlow<List<TaskItem>>(emptyList())
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0=Today, 1=Upcoming, 2=Completed
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    init { loadTasks() }

    private fun loadTasks() = viewModelScope.launch {
        _isLoading.value = true
        _tasks.value = repo.loadTasks()
        _isLoading.value = false
    }

    private fun persist(list: List<TaskItem>, privacyActive: Boolean, hideDetails: Boolean) {
        viewModelScope.launch {
            repo.saveTasks(list)
            list.filter { !it.completed }.forEach {
                scheduleNotification(getApplication(), it, hideDetails, privacyActive)
            }
        }
    }

    fun addTask(task: TaskItem, privacyActive: Boolean, hideDetails: Boolean) {
        val updated = _tasks.value + task
        _tasks.value = updated
        persist(updated, privacyActive, hideDetails)
    }

    fun toggleCompletion(id: String, privacyActive: Boolean, hideDetails: Boolean) {
        val updated = _tasks.value.map {
            if (it.id == id) it.copy(completed = !it.completed) else it
        }
        _tasks.value = updated
        // Cancel notification if now completed
        updated.find { it.id == id }?.let {
            if (it.completed) cancelNotification(getApplication(), id)
        }
        persist(updated, privacyActive, hideDetails)
    }

    fun deleteTask(id: String) {
        cancelNotification(getApplication(), id)
        val updated = _tasks.value.filter { it.id != id }
        _tasks.value = updated
        viewModelScope.launch { repo.saveTasks(updated) }
    }

    fun wipeAllData() = viewModelScope.launch {
        _tasks.value.forEach { cancelNotification(getApplication(), it.id) }
        _tasks.value = emptyList()
        repo.wipeAllData()
    }

    fun setSearchQuery(q: String) { _searchQuery.value = q }
    fun setTab(tab: Int) { _selectedTab.value = tab }
}
