package com.lockin.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("lockin_settings", Context.MODE_PRIVATE)

    private val _privacyModeEnabled = MutableStateFlow(prefs.getBoolean("privacyMode", false))
    val privacyModeEnabled: StateFlow<Boolean> = _privacyModeEnabled.asStateFlow()

    private val _hideNotifDetails = MutableStateFlow(prefs.getBoolean("hideNotifDetails", false))
    val hideNotifDetails: StateFlow<Boolean> = _hideNotifDetails.asStateFlow()

    private val _defaultPrivateTasks = MutableStateFlow(prefs.getBoolean("defaultPrivate", false))
    val defaultPrivateTasks: StateFlow<Boolean> = _defaultPrivateTasks.asStateFlow()

    private val _faceIdEnabled = MutableStateFlow(prefs.getBoolean("faceId", false))
    val faceIdEnabled: StateFlow<Boolean> = _faceIdEnabled.asStateFlow()

    private val _autoLockMinutes = MutableStateFlow(prefs.getInt("autoLock", 5))
    val autoLockMinutes: StateFlow<Int> = _autoLockMinutes.asStateFlow()

    fun togglePrivacyMode() {
        _privacyModeEnabled.value = !_privacyModeEnabled.value
        prefs.edit().putBoolean("privacyMode", _privacyModeEnabled.value).apply()
    }

    fun toggleHideNotifDetails() {
        _hideNotifDetails.value = !_hideNotifDetails.value
        prefs.edit().putBoolean("hideNotifDetails", _hideNotifDetails.value).apply()
    }

    fun toggleDefaultPrivate() {
        _defaultPrivateTasks.value = !_defaultPrivateTasks.value
        prefs.edit().putBoolean("defaultPrivate", _defaultPrivateTasks.value).apply()
    }

    fun toggleFaceId() {
        _faceIdEnabled.value = !_faceIdEnabled.value
        prefs.edit().putBoolean("faceId", _faceIdEnabled.value).apply()
    }

    fun setAutoLock(minutes: Int) {
        _autoLockMinutes.value = minutes
        prefs.edit().putInt("autoLock", minutes).apply()
    }
}
