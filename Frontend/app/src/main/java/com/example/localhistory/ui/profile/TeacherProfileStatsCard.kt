package com.example.localhistory.ui.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.localhistory.model.response.User

@Composable
fun TeacherProfileStatsCard(
    user: User.Teacher,
    modifier: Modifier = Modifier
) {
    // Intentionally empty for now. Keeping teacher role-specific profile content
    // behind this component makes future TeacherDTO fields straightforward to add.
}
