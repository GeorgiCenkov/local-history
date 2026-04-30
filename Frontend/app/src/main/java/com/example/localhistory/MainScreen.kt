package com.example.localhistory

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.localhistory.model.response.Role
import com.example.localhistory.model.response.User
import com.example.localhistory.navigation.Screen
import com.example.localhistory.ui.components.BottomNavBar
import com.example.localhistory.ui.discover.DiscoverScreen
import com.example.localhistory.ui.home.HomeScreen
import com.example.localhistory.ui.homework.HomeworkScreen
import com.example.localhistory.ui.landmark.TeacherLandmarksScreen
import com.example.localhistory.ui.landmark.landmarkdetails.LandmarkDetailRoute
import com.example.localhistory.ui.landmark.route.LandmarkCreateRoute
import com.example.localhistory.ui.landmark.route.LandmarkEditRoute
import com.example.localhistory.ui.profile.ProfileScreen

@Composable
fun MainScreen(
    availableScreens: List<Screen>,
    user: User,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val startDestination = availableScreens.first()  // home for students or teachers

    // Derive current screen from the back stack
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    var currentRootRoute by rememberSaveable { androidx.compose.runtime.mutableStateOf(startDestination.route) }

    val bottomRoutes = availableScreens.map { it.route }

    LaunchedEffect(currentRoute) {
        if (currentRoute in bottomRoutes) {
            currentRootRoute = currentRoute ?: startDestination.route
        }
    }

    // Subpages such as landmark details do not have their own bottom-nav item.
    // Keep highlighting the root tab that opened them instead of falling back to Home.
    val currentScreen = availableScreens.firstOrNull { it.route == currentRoute }
        ?: availableScreens.firstOrNull { it.route == currentRootRoute }
        ?: startDestination

    fun routeIndex(route: String?): Int {
        return bottomRoutes.indexOf(route)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                availableScreens = availableScreens,
                onScreenSelected = { screen ->
                    navController.navigate(screen.route) {
                        popUpTo(startDestination.route) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
    ) { padding ->

        // Use a nested nav host to handle navigation between the main pages
        NavHost(
            navController = navController,
            startDestination = startDestination.route,
            modifier = Modifier.padding(padding),
            enterTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route

                if (from in bottomRoutes && to in bottomRoutes) {
                    val forward = routeIndex(to) > routeIndex(from)

                    slideInHorizontally(
                        animationSpec = tween(250),
                        initialOffsetX = { if (forward) it else -it }
                    )
                } else {
                    slideInVertically(
                        animationSpec = tween(250),
                        initialOffsetY = { it }
                    )
                }
            },
            exitTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route

                if (from in bottomRoutes && to in bottomRoutes) {
                    val forward = routeIndex(to) > routeIndex(from)

                    slideOutHorizontally(
                        animationSpec = tween(250),
                        targetOffsetX = { if (forward) -it else it }
                    )
                } else {
                    fadeOut(animationSpec = tween(120))
                }
            },
            popEnterTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route

                if (from in bottomRoutes && to in bottomRoutes) {
                    val forward = routeIndex(to) > routeIndex(from)

                    slideInHorizontally(
                        animationSpec = tween(250),
                        initialOffsetX = { if (forward) it else -it }
                    )
                } else {
                    slideInVertically(
                        animationSpec = tween(250),
                        initialOffsetY = { -it }
                    )
                }
            },
            popExitTransition = {
                val from = initialState.destination.route
                val to = targetState.destination.route

                if (from in bottomRoutes && to in bottomRoutes) {
                    val forward = routeIndex(to) > routeIndex(from)

                    slideOutHorizontally(
                        animationSpec = tween(250),
                        targetOffsetX = { if (forward) -it else it }
                    )
                } else {
                    slideOutVertically(
                        animationSpec = tween(250),
                        targetOffsetY = { it }
                    )
                }
            }
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Discover.route) {
                DiscoverScreen(
                    onOpenLandmark = { landmarkId ->
                        navController.navigate(Screen.LandmarkDetail.createRoute(landmarkId))
                    }
                )
            }

            composable(Screen.Homework.route) {
                HomeworkScreen()
            }

            composable(Screen.TeacherLandmarks.route) {
                TeacherLandmarksScreen(
                    onOpenLandmark = { landmarkId ->
                        navController.navigate(Screen.LandmarkDetail.createRoute(landmarkId))
                    },
                    onEditLandmark = { landmarkId ->
                        navController.navigate(Screen.LandmarkEdit.createRoute(landmarkId))
                    },
                    onCreateLandmark = {
                        navController.navigate(Screen.LandmarkCreate.route)
                    }
                )
            }

            composable(Screen.LandmarkDetail.route) { backStackEntry ->
                val landmarkId = backStackEntry.arguments
                    ?.getString("landmarkId")
                    ?.toLongOrNull()
                    ?: return@composable

                LandmarkDetailRoute(
                    landmarkId = landmarkId,
                    onBack = { navController.popBackStack() },
                    canSubmitVisit = user.role == Role.STUDENT,
                    onEdit = if (user.role == Role.TEACHER) {
                        {
                            navController.navigate(Screen.LandmarkEdit.createRoute(landmarkId))
                        }
                    } else {
                        null
                    }
                )
            }

            composable(Screen.LandmarkEdit.route) { backStackEntry ->
                val landmarkId = backStackEntry.arguments
                    ?.getString("landmarkId")
                    ?.toLongOrNull()
                    ?: return@composable

                LandmarkEditRoute (
                    landmarkId = landmarkId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

            composable(Screen.LandmarkCreate.route) {
                LandmarkCreateRoute (
                    onBack = { navController.popBackStack() },
                    onCreated = { navController.popBackStack() }
                )
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
