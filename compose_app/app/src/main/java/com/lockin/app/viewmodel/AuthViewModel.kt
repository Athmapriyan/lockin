package com.lockin.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Manages email login (step 1) + 4-digit PIN (step 2) */
class AuthViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("lockin_auth", Context.MODE_PRIVATE)

    // Step 1 — email verified
    private val _emailVerified = MutableStateFlow(false)
    val emailVerified: StateFlow<Boolean> = _emailVerified.asStateFlow()

    // Step 2 — app unlocked
    private val _isUnlocked = MutableStateFlow(false)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    // Entered email
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    // Shake on wrong pin
    private val _pinError = MutableStateFlow(false)
    val pinError: StateFlow<Boolean> = _pinError.asStateFlow()

    fun setEmail(e: String) { _email.value = e }

    /** Mock OTP — accept any 6-digit code */
    fun submitOtp(otp: String): Boolean {
        return if (otp.length == 6) {
            _emailVerified.value = true
            true
        } else false
    }

    /** Hardcoded PIN = 1234 (per iOS spec) */
    fun submitPin(pin: String): Boolean {
        val savedPin = prefs.getString("userPin", "1234")
        return if (pin == savedPin) {
            _isUnlocked.value = true
            _pinError.value = false
            true
        } else {
            _pinError.value = true
            false
        }
    }

    fun resetPinError() { _pinError.value = false }
    fun unlockViaBiometrics() { _isUnlocked.value = true }
    fun lock() { _isUnlocked.value = false }
    fun fullLogout() {
        _emailVerified.value = false
        _isUnlocked.value = false
        _email.value = ""
    }
}
