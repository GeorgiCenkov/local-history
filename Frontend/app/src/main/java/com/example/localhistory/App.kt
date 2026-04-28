package com.example.localhistory

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.localhistory.navigation.Screen
import com.example.localhistory.ui.home.HomeScreen
import com.example.localhistory.ui.profile.ProfileScreen
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.localhistory.ui.components.BottomNavBar
import com.example.localhistory.ui.components.screens
import com.example.localhistory.ui.discover.DiscoverScreen
import com.example.localhistory.ui.homework.HomeworkScreen
import com.example.localhistory.ui.onboarding.OnboardingScreen
import com.example.localhistory.ui.theme.LocalHistoryTheme
import com.example.localhistory.utils.currentLanguage
import com.example.localhistory.utils.updateLocale
import androidx.core.content.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.ui.login.LoginScreen
import com.example.localhistory.ui.login.LoginState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.localhistory.ui.login.LoginViewModel

@Composable
fun App() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var showOnboarding by remember {
        mutableStateOf(prefs.getBoolean("show_onboarding", true))
    }

    // after onboarding is done, show login before the main app
    var showLogin by remember { mutableStateOf(true) }

    var currentScreen: Screen by remember { mutableStateOf(Screen.Home) }

    // rebuild context whenever language changes
    val localizedContext = remember(currentLanguage) {
        updateLocale(context, currentLanguage)
    }

    // provide the localized context to the whole tree
    CompositionLocalProvider(LocalContext provides localizedContext) {
        LocalHistoryTheme {
            if (showOnboarding) {
                OnboardingScreen(
                    onFinished = {
                        prefs.edit { putBoolean("show_onboarding", false) }
                        showOnboarding = false
                    }
                )
            } else if (showLogin) {
                val loginViewModel: LoginViewModel = viewModel()
                val loginState by loginViewModel.loginState.collectAsStateWithLifecycle()

                // react to state changes
                LaunchedEffect(loginState) {
                    when (loginState) {
                        is LoginState.Success -> showLogin = false  // navigate into the app
                        else -> Unit
                    }
                }

                LoginScreen(
                    onLoginClick = { email, password ->
                        loginViewModel.login(email, password)  // screen just reports, VM does the work
                    },
                    onRegisterClick = {
                        // TODO: navigate to register screen
                    }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        BottomNavBar(
                            currentScreen = currentScreen,
                            onScreenSelected = { currentScreen = it }
                        )
                    }
                ) { padding ->
                    AnimatedContent( // smooth animations between screens
                        targetState = currentScreen,
                        transitionSpec = {
                            // slide direction based on position in the nav order
                            val targetIndex = screens.indexOf(targetState)
                            val initialIndex = screens.indexOf(initialState)
                            val goingForward = targetIndex > initialIndex

                            fadeIn(tween(300)) + slideInHorizontally(
                                initialOffsetX = { if (goingForward) it else -it },
                                animationSpec = tween(300)
                            ) togetherWith fadeOut(tween(200)) + slideOutHorizontally(
                                targetOffsetX = { if (goingForward) -it else it },
                                animationSpec = tween(300)
                            )
                        },
                        modifier = Modifier.padding(padding)
                    ) { screen ->
                        when (screen) { // redirect to the correct screen
                            Screen.Home -> HomeScreen()
                            Screen.Discover -> DiscoverScreen()
                            Screen.Homework -> HomeworkScreen()
                            Screen.Profile -> ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}