package com.example.localhistory.navigation
import androidx.annotation.StringRes
import com.example.localhistory.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Home    : Screen("home",    R.string.nav_home)
    data object Discover    : Screen("discover",    R.string.nav_discover)
    data object Homework    : Screen("homework",    R.string.nav_homework)
    data object Profile : Screen("profile", R.string.nav_profile)
}