package com.example.localhistory.ui.homework

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localhistory.R

// Teacher homework route that switches between homework list, create form, and assignment detail.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen() {
    val viewModel: TeacherHomeworkViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when {
                            state.isFormOpen -> stringResource(R.string.homework_create_title)
                            state.selectedHomework != null -> state.selectedHomework?.title.orEmpty()
                            else -> stringResource(R.string.homework_title)
                        }
                    )
                },
                navigationIcon = {
                    if (state.isFormOpen || state.selectedHomework != null) {
                        IconButton(
                            onClick = {
                                if (state.isFormOpen) viewModel.closeForm()
                                else viewModel.clearSelection()
                            }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadInitialData) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = stringResource(R.string.action_refresh)
                        )
                    }
                    if (!state.isFormOpen && state.selectedHomework == null) {
                        FilledTonalIconButton(onClick = viewModel::openCreateForm) {
                            Icon(
                                Icons.Outlined.Add,
                                contentDescription = stringResource(R.string.homework_action_add)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isFormOpen -> HomeworkForm(
                    state = state,
                    onFormChange = viewModel::updateForm,
                    onCancel = viewModel::closeForm,
                    onSave = viewModel::saveHomework
                )
                state.selectedHomework != null -> HomeworkAssignmentContent(
                    state = state,
                    onSearchChange = viewModel::updateStudentSearch,
                    onStudentSelected = viewModel::addStudent,
                    onStudentRemoved = viewModel::removeStudent,
                    onAssign = viewModel::assignSelectedStudents
                )
                else -> HomeworkListContent(
                    state = state,
                    onCreate = viewModel::openCreateForm,
                    onOpen = viewModel::selectHomework,
                    onDelete = { viewModel.deleteHomework(it.id) }
                )
            }
        }
    }
}
