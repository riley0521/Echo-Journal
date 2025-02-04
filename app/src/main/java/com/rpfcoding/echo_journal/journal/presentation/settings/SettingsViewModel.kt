package com.rpfcoding.echo_journal.journal.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.echo_journal.journal.domain.GetUnselectedTopicsUseCase
import com.rpfcoding.echo_journal.journal.domain.JournalPreferenceManager
import com.rpfcoding.echo_journal.journal.domain.LocalJournalDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val dataSource: LocalJournalDataSource,
    private val prefManager: JournalPreferenceManager,
    private val getUnselectedTopicsUseCase: GetUnselectedTopicsUseCase
): ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state = _state
        .onStart {
            initObservers()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            SettingsState()
        )

    private fun initObservers() {
        prefManager
            .get()
            .onEach { journalPreference ->
                _state.update {
                    it.copy(
                        selectedMood = journalPreference.selectedMood,
                        selectedTopics = journalPreference.selectedTopics
                    )
                }
            }.launchIn(viewModelScope)

        combine(
            _state,
            dataSource.getAllTopics()
        ) { curState, topics ->
            val topicInformation = getUnselectedTopicsUseCase(
                query = curState.inputTopic,
                shouldTakeItems = curState.isTopicFieldFocused,
                selectedTopics = curState.selectedTopics,
                allTopics = topics
            )
            _state.update {
                it.copy(
                    unselectedTopics = topicInformation.unselectedTopics,
                    isNewTopic = topicInformation.isNewTopic
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.OnAddNewTopic -> {
                viewModelScope.launch {
                    val newTopic = _state
                        .value
                        .inputTopic
                        .trim()
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                    dataSource.insertTopic(newTopic)
                    updateDefaultTopics(TopicsAction.OnAdd(newTopic))

                    _state.update {
                        it.copy(
                            inputTopic = ""
                        )
                    }
                }
            }
            is SettingsAction.OnDeleteTopic -> {
                updateDefaultTopics(TopicsAction.OnDelete(action.topic))
            }
            is SettingsAction.OnInputTopic -> {
                _state.update { it.copy(inputTopic = action.value) }
            }
            is SettingsAction.OnSelectMood -> {
                viewModelScope.launch {
                    prefManager.setDefaultMood(action.mood)
                }
            }
            is SettingsAction.OnSelectTopic -> {
                updateDefaultTopics(TopicsAction.OnAdd(action.topic))
            }
            is SettingsAction.OnTopicFieldFocusChange -> {
                _state.update { it.copy(isTopicFieldFocused = action.isFocused) }
            }
            else -> Unit
        }
    }

    private fun updateDefaultTopics(action: TopicsAction) = viewModelScope.launch {
        val selectedTopics = _state.value.selectedTopics
        val updatedTopics = when (action) {
            is TopicsAction.OnAdd -> selectedTopics + action.topic
            is TopicsAction.OnDelete -> selectedTopics - action.topic
        }
        prefManager.setDefaultTopics(updatedTopics)
    }

    private sealed interface TopicsAction {
        data class OnAdd(val topic: String): TopicsAction
        data class OnDelete(val topic: String): TopicsAction
    }
}