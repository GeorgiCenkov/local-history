package com.example.localhistory.ui.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.localhistory.ui.components.LoadingContent

// Scrollable leaderboard result list.
@Composable
fun LeaderboardList(state: LeaderboardUiState) {
    when {
        state.isLoading && state.rows.isEmpty() -> LoadingContent(Modifier.fillMaxSize())
        else -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.rows, key = { it.studentId }) { row ->
                LeaderboardRow(row = row)
            }
        }
    }
}
