package com.example.localhistory.navigation
import androidx.annotation.StringRes
import com.example.localhistory.R

sealed class Screen(val route: String, @StringRes val labelRes: Int) {
    data object Home    : Screen("home",    R.string.nav_home)
    data object Discover    : Screen("discover",    R.string.nav_discover)
    data object Homework    : Screen("homework",    R.string.nav_homework)
    data object TeacherLandmarks : Screen("teacher_landmarks", R.string.nav_landmarks)
    data object Profile : Screen("profile", R.string.nav_profile)

    data object LandmarkDetail : Screen(
        "landmark_detail/{landmarkId}",
        R.string.nav_landmark_detail
    ) {
        fun createRoute(landmarkId: Long) = "landmark_detail/$landmarkId"
    }

    data object LandmarkEdit : Screen(
        "landmark_edit/{landmarkId}",
        R.string.nav_landmark_edit
    ) {
        fun createRoute(landmarkId: Long) = "landmark_edit/$landmarkId"
    }

    data object LandmarkCreate : Screen(
        "landmark_create",
        R.string.nav_landmark_create
    )

    data object QuizCreate : Screen(
        "quiz_create/{landmarkId}",
        R.string.nav_quiz_create
    ) {
        fun createRoute(landmarkId: Long) = "quiz_create/$landmarkId"
    }

    data object QuizEdit : Screen(
        "quiz_edit/{quizId}",
        R.string.nav_quiz_edit
    ) {
        fun createRoute(quizId: Long) = "quiz_edit/$quizId"
    }

    data object QuizTake : Screen(
        "quiz_take/{quizId}",
        R.string.nav_quiz_take
    ) {
        fun createRoute(quizId: Long) = "quiz_take/$quizId"
    }
}
