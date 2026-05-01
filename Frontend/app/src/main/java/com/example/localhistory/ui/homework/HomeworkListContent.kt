package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.localhistory.R
import com.example.localhistory.model.response.HomeworkDTO
import com.example.localhistory.ui.components.LoadingContent

// Displays the teacher's homework list using the same card rhythm as landmarks.
@Composable
fun HomeworkListContent(
    state: TeacherHomeworkUiState,
    onCreate: () -> Unit,
    onOpen: (HomeworkDTO) -> Unit,
    onDelete: (HomeworkDTO) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        HomeworkMessage(state)

        when {
            state.isLoading && state.homework.isEmpty() -> LoadingContent()
            state.homework.isEmpty() -> EmptyHomework(onAction = onCreate)
            else -> LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = stringResource(R.string.homework_subtitle, state.homework.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                items(state.homework, key = { it.id }) { homework ->
                    HomeworkCard(
                        homework = homework,
                        onOpen = { onOpen(homework) },
                        onDelete = { onDelete(homework) }
                    )
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }
}
