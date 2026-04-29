package com.example.localhistory

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.localhistory.model.response.Role
import com.example.localhistory.navigation.Screen
import com.example.localhistory.ui.components.screens
import com.example.localhistory.ui.components.teacherScreens
import com.example.localhistory.ui.login.LoginScreen
import com.example.localhistory.ui.onboarding.OnboardingScreen
import com.example.localhistory.ui.register.RegisterScreen
import com.example.localhistory.ui.theme.LocalHistoryTheme
import com.example.localhistory.utils.currentLanguage
import com.example.localhistory.utils.updateLocale


@Composable
fun App() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val appViewModel: AppViewModel = viewModel()

    var showOnboarding by remember {
        mutableStateOf(prefs.getBoolean("show_onboarding", true))
    }

    val authSession by appViewModel.authSession.collectAsStateWithLifecycle()
    if (authSession == null) return

    val user = authSession?.user
    val showLogin = authSession?.accessToken == null
    var currentScreen: Screen by remember { mutableStateOf(Screen.Home) }

    var showRegister by remember { mutableStateOf(false) }

    val localizedContext = remember(currentLanguage) {
        updateLocale(context, currentLanguage)
    }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        LocalHistoryTheme {
            val availableScreens = if (user?.role == Role.TEACHER) teacherScreens else screens
            val visibleScreen = currentScreen.takeIf { it in availableScreens } ?: availableScreens.first()

            LaunchedEffect(showLogin, user?.role) {
                if (!showLogin && currentScreen !in availableScreens) {
                    currentScreen = availableScreens.first()
                }
            }

            LaunchedEffect(showLogin, user) {
                if (!showLogin && user == null) {
                    appViewModel.logout()
                }
            }

            when {
                showOnboarding -> OnboardingScreen(
                    onFinished = {
                        prefs.edit { putBoolean("show_onboarding", false) }
                        showOnboarding = false
                    }
                )
                showRegister -> RegisterScreen(
                    onRegisterSuccess = { showRegister = false },  // token saved, DataStore triggers navigation
                    onBackToLogin = { showRegister = false }
                )
                showLogin -> LoginScreen(onRegisterClick = { showRegister = true })
                user == null -> Unit
                else -> MainScreen(
                    currentScreen = visibleScreen,
                    availableScreens = availableScreens,
                    user = user,
                    onLogout = appViewModel::logout
                ) { currentScreen = it }
            }
        }
    }
}
