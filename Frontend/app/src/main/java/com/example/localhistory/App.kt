package com.example.localhistory

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.localhistory.model.response.Role
import com.example.localhistory.ui.components.screens
import com.example.localhistory.ui.components.teacherScreens
import com.example.localhistory.ui.login.LoginScreen
import com.example.localhistory.ui.onboarding.OnboardingScreen
import com.example.localhistory.ui.register.RegisterScreen


@Composable
fun App() {
    val appViewModel: AppViewModel = hiltViewModel()
    val navController = rememberNavController()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var showOnboarding by remember {
        mutableStateOf(prefs.getBoolean("show_onboarding", true))
    }

    val authSession by appViewModel.authSession.collectAsStateWithLifecycle()

    val user = authSession?.user

    NavHost(
        navController = navController,
        startDestination = "loading"
    ) {
        composable("loading") {
            val isSessionLoaded by appViewModel.isSessionLoaded.collectAsStateWithLifecycle()
            // Decide start destination based on the saved onboarding state and authentication status
            LaunchedEffect(showOnboarding, isSessionLoaded, authSession?.accessToken) {
                if (!isSessionLoaded) return@LaunchedEffect

                val destination = when {
                    showOnboarding -> "onboarding"
                    authSession?.accessToken != null -> "main"
                    else -> "login"
                }

                navController.navigate(destination) {
                    popUpTo("loading") { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable("onboarding") {
            OnboardingScreen(
                onFinished = {
                    prefs.edit { putBoolean("show_onboarding", false) }
                    showOnboarding = false
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("main") {
            val currentUser = user

            // Last line of defense to ensure we don't show the main screen without a user
            if (currentUser == null) {
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                return@composable
            }

            val availableScreens =
                if (currentUser.role == Role.TEACHER) teacherScreens else screens


            MainScreen(
                availableScreens = availableScreens,
                user = currentUser,
                onLogout = appViewModel::logout,
            )
        }
    }
}
