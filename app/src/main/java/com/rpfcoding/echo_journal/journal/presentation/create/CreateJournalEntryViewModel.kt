package com.rpfcoding.echo_journal.journal.presentation.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.echo_journal.core.domain.audio.AudioPlayer
import com.rpfcoding.echo_journal.core.domain.file.FileManager
import com.rpfcoding.echo_journal.core.presentation.ui.UiText
import com.rpfcoding.echo_journal.journal.domain.GetUnselectedTopicsUseCase
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.JournalPreferenceManager
import com.rpfcoding.echo_journal.journal.domain.LocalJournalDataSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CreateJournalEntryViewModel(
    private val dataSource: LocalJournalDataSource,
    private val audioPlayer: AudioPlayer,
    private val fileManager: FileManager,
    private val journalPreferenceManager: JournalPreferenceManager,
    private val getUnselectedTopicsUseCase: GetUnselectedTopicsUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val id = savedStateHandle.get<String>("id")!!
    private val fileUri = savedStateHandle.get<String>("fileUri")!!

    private var isLoaded = false
    private val _state = MutableStateFlow(CreateJournalEntryState())
    val state = _state
        .onStart {
            if (!isLoaded) {
                loadDefaultMoodAndTopics()
                loadRecordingFile()
            }
            initObservers()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            CreateJournalEntryState()
        )
    private val _eventChannel = Channel<CreateJournalEntryEvent>()
    val events = _eventChannel.receiveAsFlow()

    private suspend fun loadDefaultMoodAndTopics() {
        val journalPref = journalPreferenceManager.get().firstOrNull()
        _state.update {
            it.copy(
                selectedMood = journalPref?.selectedMood,
                selectedTopics = journalPref?.selectedTopics.orEmpty()
            )
        }
    }

    private fun loadRecordingFile() {
        val recordingFile = fileManager.getFileFromUri(fileUri)
        audioPlayer.play(
            file = recordingFile,
            onComplete = {
                _state.update { it.copy(isPlaying = false) }
            },
            shouldPlayImmediately = false
        )

        val durationOfAudio = fileManager.getDurationOfAudioFile(recordingFile)
        if (durationOfAudio > 0) {
            _state.update { it.copy(maxPlaybackInSeconds = durationOfAudio) }
        }
        isLoaded = true
    }

    private fun initObservers() {
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

        combine(
            _state,
            audioPlayer.curPlaybackInSeconds
        ) { curState, curPlaybackInSeconds ->
            if (curState.isPlaying) {
                _state.update {
                    it.copy(
                        curPlaybackInSeconds = curPlaybackInSeconds
                    )
                }
            }
        }.launchIn(viewModelScope)

        _state.onEach { curState ->
            val canSave = curState.title.isNotBlank() && curState.selectedMood != null
            _state.update { it.copy(canSave = canSave) }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: CreateJournalEntryAction) {
        when (action) {
            is CreateJournalEntryAction.OnToggleCancelDialog -> {
                _state.update { it.copy(isCancelCreateJournalEntryDialogOpened = action.isOpen) }
            }
            CreateJournalEntryAction.OnCancelCreateJournalEntry -> {
                viewModelScope.launch {
                    // Delete the created file to not consume space.
                    fileManager.getFileFromUri(fileUri).delete()

                    _eventChannel.send(CreateJournalEntryEvent.NavigateBack)
                }
            }

            is CreateJournalEntryAction.OnDescriptionChange -> {
                _state.update { it.copy(description = action.value) }
            }
            is CreateJournalEntryAction.OnInputTopic -> {
                _state.update { it.copy(inputTopic = action.value) }
            }
            is CreateJournalEntryAction.OnTopicFieldFocusChange -> {
                _state.update { it.copy(isTopicFieldFocused = action.isFocused) }
            }
            is CreateJournalEntryAction.OnDeleteTopic -> {
                _state.update {
                    it.copy(selectedTopics = it.selectedTopics - action.value)
                }
            }
            is CreateJournalEntryAction.OnSelectTopic -> {
                _state.update {
                    it.copy(
                        selectedTopics = it.selectedTopics + action.value,
                        inputTopic = ""
                    )
                }
            }
            CreateJournalEntryAction.OnAddNewTopic -> {
                viewModelScope.launch {
                    val newTopic = _state
                        .value
                        .inputTopic
                        .trim()
                        .lowercase()
                        .replaceFirstChar { it.uppercase() }
                    dataSource.insertTopic(newTopic)

                    _state.update {
                        it.copy(
                            selectedTopics = it.selectedTopics + newTopic,
                            inputTopic = ""
                        )
                    }
                }
            }
            is CreateJournalEntryAction.OnToggleSelectMoodBottomSheet -> {
                _state.update { it.copy(isSelectMoodBottomSheetOpened = action.isOpen) }
            }
            is CreateJournalEntryAction.OnSelectMood -> {
                _state.update { it.copy(selectedMood = action.mood) }
            }
            is CreateJournalEntryAction.OnTitleChange -> {
                _state.update { it.copy(title = action.value) }
            }
            CreateJournalEntryAction.OnToggleAudioPlayer -> {
                val shouldPlay = !_state.value.isPlaying
                if (shouldPlay) {
                    audioPlayer.resume()
                } else {
                    audioPlayer.pause()
                }
                _state.update { it.copy(isPlaying = shouldPlay) }
            }
            CreateJournalEntryAction.OnSaveClick -> {
                audioPlayer.stopAndResetPlayer()
                viewModelScope.launch {
                    try {
                        val state = _state.value
                        val title = state.title
                        val description = state.description
                        val selectedMood = state.selectedMood!!
                        val maxPlaybackInSeconds = state.maxPlaybackInSeconds

                        val newJournal = Journal(
                            id = id,
                            mood = selectedMood,
                            title = title,
                            description = description,
                            recordingUri = fileUri,
                            maxPlaybackInSeconds = maxPlaybackInSeconds,
                            dateTimeCreated = LocalDateTime.now(),
                            topics = state.selectedTopics
                        )
                        dataSource.upsert(newJournal)
                        _eventChannel.send(CreateJournalEntryEvent.Success)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        _eventChannel.send(CreateJournalEntryEvent.Error(UiText.DynamicString("Something went wrong.")))
                    }
                }
            }
            is CreateJournalEntryAction.OnSeekCurrentPlayback -> {
                val millis = action
                    .seconds
                    .toDuration(DurationUnit.SECONDS)
                    .inWholeMilliseconds
                audioPlayer.seekTo(millis.toInt())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stopAndResetPlayer()
    }
}