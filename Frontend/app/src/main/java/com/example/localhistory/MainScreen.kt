package com.example.localhistory

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.localhistory.model.response.UserDTO
import com.example.localhistory.navigation.Screen
import com.example.localhistory.ui.components.BottomNavBar
import com.example.localhistory.ui.discover.DiscoverScreen
import com.example.localhistory.ui.home.HomeScreen
import com.example.localhistory.ui.homework.HomeworkScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksScreen
import com.example.localhistory.ui.profile.ProfileScreen
import androidx.activity.compose.BackHandler

@Composable
fun MainScreen(
    currentScreen: Screen,
    availableScreens: List<Screen>,
    user: UserDTO,
    onLogout: () -> Unit,
    onScreenSelected: (Screen) -> Unit
) {
    // Handle the native android back gesture
    BackHandler(enabled = currentScreen != Screen.Home) {
        onScreenSelected(Screen.Home)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                availableScreens = availableScreens,
                onScreenSelected = onScreenSelected
            )
        }
    ) { padding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val targetIndex = availableScreens.indexOf(targetState)
                val initialIndex = availableScreens.indexOf(initialState)
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
            when (screen) {
                Screen.Home -> HomeScreen()
                Screen.Discover -> DiscoverScreen()
                Screen.Homework -> HomeworkScreen()
                Screen.TeacherLandmarks -> TeacherLandmarksScreen()
                Screen.Profile -> ProfileScreen(user = user, onLogout = onLogout)
            }
        }
    }
}
