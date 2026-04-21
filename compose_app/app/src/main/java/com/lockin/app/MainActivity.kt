package com.lockin.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lockin.app.ui.screens.MainTabView
import com.lockin.app.ui.screens.auth.AppLockScreen
import com.lockin.app.ui.screens.auth.LoginScreen
import com.lockin.app.ui.screens.auth.OtpScreen
import com.lockin.app.ui.theme.LockInTheme
import com.lockin.app.viewmodel.AuthViewModel
import com.lockin.app.viewmodel.SettingsViewModel
import com.lockin.app.viewmodel.TaskViewModel

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockInTheme {
                LockInApp()
            }
        }
    }
}

enum class AuthStep { LOGIN, OTP, APP_LOCK, DASHBOARD }

@Composable
fun LockInApp() {
    val authViewModel: AuthViewModel         = viewModel()
    val settingsViewModel: SettingsViewModel  = viewModel()
    val taskViewModel: TaskViewModel          = viewModel()

    val emailVerified by authViewModel.emailVerified.collectAsState()
    val isUnlocked    by authViewModel.isUnlocked.collectAsState()

    val step = when {
        !emailVerified              -> AuthStep.LOGIN
        emailVerified && !isUnlocked -> AuthStep.APP_LOCK
        else                        -> AuthStep.DASHBOARD
    }

    // Track whether we need the OTP step
    var needsOtp by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = if (needsOtp && !emailVerified) AuthStep.OTP else step,
        transitionSpec = {
            slideInVertically(tween(380)) { it / 8 } + fadeIn(tween(380)) togetherWith
            fadeOut(tween(200))
        },
        label = "app_root"
    ) { s ->
        when (s) {
            AuthStep.LOGIN -> LoginScreen(
                authViewModel = authViewModel,
                onNext = { needsOtp = true }
            )
            AuthStep.OTP -> OtpScreen(
                authViewModel = authViewModel,
                onSuccess = { needsOtp = false }
            )
            AuthStep.APP_LOCK -> AppLockScreen(
                authViewModel     = authViewModel,
                settingsViewModel = settingsViewModel
            )
            AuthStep.DASHBOARD -> MainTabView(
                taskViewModel     = taskViewModel,
                settingsViewModel = settingsViewModel,
                authViewModel     = authViewModel
            )
        }
    }
}
