package com.rpfcoding.echo_journal.journal.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.MoodItem
import com.rpfcoding.echo_journal.journal.presentation.components.TopicFilterDropdown
import com.rpfcoding.echo_journal.journal.presentation.components.TopicsAndInput
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                SettingsAction.OnNavigateBack -> onNavigateBack()
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    var containerHeight by remember {
        mutableIntStateOf(0)
    }
    var topicsHeight by remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction(SettingsAction.OnNavigateBack)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(14.dp)
            ) {
                Text(
                    text = "My Mood",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Select default mood to apply to all new entries",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Mood.entries.forEach { mood ->
                        MoodItem(
                            mood = mood,
                            isSelected = state.selectedMood == mood,
                            onClick = {
                                onAction(SettingsAction.OnSelectMood(mood))
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(14.dp)
                        .onGloballyPositioned { 
                            containerHeight = it.size.height
                        }
                ) {
                    Text(
                        text = "My Topics",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Select default topics to apply to all new entries",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                topicsHeight = it.size.height
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TopicsAndInput(
                            color = MaterialTheme.colorScheme.tertiary,
                            topics = state.selectedTopics,
                            input = state.inputTopic,
                            onInputChange = {
                                onAction(SettingsAction.OnInputTopic(it))
                            },
                            onDelete = {
                                onAction(SettingsAction.OnDeleteTopic(it))
                            },
                            onFocusChange = {
                                onAction(SettingsAction.OnTopicFieldFocusChange(it))
                            },
                            showAddButton = true
                        )
                    }
                }
                TopicFilterDropdown(
                    query = state.inputTopic,
                    filteredTopics = state.unselectedTopics,
                    isNewTopic = state.isNewTopic,
                    onTopicClick = {
                        onAction(SettingsAction.OnSelectTopic(it))
                    },
                    onCreateNewTopicClick = {
                        onAction(SettingsAction.OnAddNewTopic)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            IntOffset(0, topicsHeight + containerHeight + 16)
                        }
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    EchoJournalTheme {
        SettingsScreen(
            state = SettingsState(
                isTopicFieldFocused = true,
                unselectedTopics = setOf("Java", "Jack", "Janitor"),
                selectedTopics = setOf("Work", "Conundrums")
            ),
            onAction = {}
        )
    }
}