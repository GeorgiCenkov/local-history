package com.example.localhistory

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.localhistory.model.response.UserDTO
import com.example.localhistory.navigation.Screen
import com.example.localhistory.ui.components.BottomNavBar
import com.example.localhistory.ui.discover.DiscoverScreen
import com.example.localhistory.ui.home.HomeScreen
import com.example.localhistory.ui.homework.HomeworkScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksScreen
import com.example.localhistory.ui.profile.ProfileScreen

@Composable
fun MainScreen(
    availableScreens: List<Screen>,
    user: UserDTO,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val startDestination = availableScreens.first()  // home for students or teachers

    // Derive current screen from the back stack
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = availableScreens.firstOrNull {
        it.route == currentBackStackEntry?.destination?.route
    } ?: startDestination


    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                availableScreens = availableScreens,
                onScreenSelected = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->

        // Use a nested nav host to handle navigation between the main pages
        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Discover.route) {
                DiscoverScreen()
            }

            composable(Screen.Homework.route) {
                HomeworkScreen()
            }

            composable(Screen.TeacherLandmarks.route) {
                TeacherLandmarksScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    user = user,
                    onLogout = onLogout
                )
            }
        }
    }
}