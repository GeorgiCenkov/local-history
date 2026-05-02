package com.example.localhistory.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.example.localhistory.navigation.Screen

val screens = listOf(
    Screen.Home,
    Screen.Discover,
    Screen.Homework,
    Screen.Profile
)

val teacherScreens = listOf(
    Screen.TeacherLandmarks,
    Screen.Homework,
    Screen.Profile
)

// filled = selected state, outlined = unselected state
data class ScreenIcon(
    val filled: ImageVector,
    val outlined: ImageVector
)

val screenIcons = mapOf(
    Screen.Home to ScreenIcon(Icons.Filled.Home, Icons.Outlined.Home),
    Screen.Discover to ScreenIcon(Icons.Filled.Explore, Icons.Outlined.Explore),
    Screen.Homework to ScreenIcon(Icons.Filled.School, Icons.Outlined.School),
    Screen.TeacherLandmarks to ScreenIcon(Icons.Filled.Map, Icons.Outlined.Map),
    Screen.Profile to ScreenIcon(Icons.Filled.Person, Icons.Outlined.Person),
)

@Composable
fun BottomNavBar(
    currentScreen: Screen,
    availableScreens: List<Screen> = screens,
    onScreenSelected: (Screen) -> Unit
) {
    NavigationBar {
        availableScreens.forEach { screen ->
            val icon = screenIcons[screen]
            val isSelected = currentScreen == screen

            NavigationBarItem(
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                icon = {
                    // toggle between filled and outlined icons based on selection state
                    icon?.let {
                        Icon(
                            imageVector = if (isSelected) it.filled else it.outlined,
                            contentDescription = stringResource(screen.labelRes)
                        )
                    }
                },
                label = { Text(stringResource(screen.labelRes)) }
            )
        }
    }
}
