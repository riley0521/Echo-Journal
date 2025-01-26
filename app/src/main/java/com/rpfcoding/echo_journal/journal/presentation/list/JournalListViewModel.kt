package com.rpfcoding.echo_journal.journal.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.echo_journal.core.domain.audio.AudioPlayer
import com.rpfcoding.echo_journal.core.domain.audio.AudioRecorder
import com.rpfcoding.echo_journal.core.domain.file.FileManager
import com.rpfcoding.echo_journal.core.presentation.ui.UiText
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.LocalJournalDataSource
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodByName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class JournalListViewModel(
    private val audioRecorder: AudioRecorder,
    private val audioPlayer: AudioPlayer,
    private val fileManager: FileManager,
    private val dataSource: LocalJournalDataSource
) : ViewModel() {

    private val _eventChannel = Channel<JournalListEvent>()
    val events = _eventChannel.receiveAsFlow()

    private val _state = MutableStateFlow(JournalListState())
    val state = _state
        .onStart {
            init()
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            JournalListState()
        )
    private val _journals = dataSource.getAll()
    private val _journalFilter = MutableStateFlow(JournalFilter())
    private val _currentJournalPlaying = MutableStateFlow<Journal?>(null)
    private val _isPlaying = MutableStateFlow(false)

    private var newJournalId: String? = null
    private var newJournalUri: String? = null

    data class JournalFilter(
        val moods: Set<Mood> = emptySet(),
        val topics: Set<String> = emptySet()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun init() {
        combine(
            _journals,
            _journalFilter
        ) { journals, filter ->
            val filteredJournals = journals.filter { item ->
                var allMoods = Mood.entries.toSet()
                var allTopics = emptySet<String>()

                if (filter.moods.isNotEmpty()) {
                    allMoods = filter.moods
                }
                if (filter.topics.isNotEmpty()) {
                    allTopics = filter.topics
                }

                if (allTopics.isNotEmpty()) {
                    item.topics.any { allTopics.contains(it) } || allMoods.contains(item.mood)
                } else {
                    allMoods.contains(item.mood)
                }
            }

            _state.update { curState ->
                curState.copy(
                    dateToJournalsMap = filteredJournals
                        .groupBy { it.dateTimeCreated.toLocalDate() }
                        .toSortedMap(compareByDescending { it })
                )
            }
        }.launchIn(viewModelScope)

        dataSource
            .getAllTopics()
            .onEach { topics ->
                _state.update { it.copy(allTopics = topics) }
            }.launchIn(viewModelScope)

        _isPlaying
            .flatMapLatest { isPlaying ->
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    _currentJournalPlaying
                } else flowOf()
            }.filterNotNull()
            .combine(audioPlayer.curPlaybackInSeconds) { currentJournal, curPlaybackInSeconds ->
                _state.update {
                    it.copy(
                        curPlaybackInSeconds = curPlaybackInSeconds,
                        currentFilePlaying = currentJournal.recordingUri
                    )
                }
            }.launchIn(viewModelScope)

        audioRecorder.durationInMillis.onEach { durationInMillis ->
            _state.update {
                it.copy(
                    durationInSeconds = durationInMillis.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun resetNewValues() {
        newJournalId = null
        newJournalUri = null
    }

    fun onAction(action: JournalListAction) {
        when (action) {
            JournalListAction.OnClearMoodFilter -> {
                _state.update { it.copy(filteredMoods = JournalFilterType.Moods(emptySet())) }
            }
            JournalListAction.OnClearTopicFilter -> {
                _state.update { it.copy(filteredTopics = JournalFilterType.Topics(emptySet())) }
            }
            is JournalListAction.OnToggleMoodFilter -> {
                _journalFilter.update {
                    it.copy(
                        moods = action.filter.moods.mapNotNull { moodUi ->
                            if (moodUi.isSelected) {
                                getMoodByName(moodUi.name)
                            } else null
                        }.toSet()
                    )
                }
            }
            is JournalListAction.OnToggleTopicFilter -> {
                _journalFilter.update {
                    it.copy(
                        topics = action.filter.topics.mapNotNull { topic ->
                            if (topic.isSelected) {
                                topic.name
                            } else null
                        }.toSet()
                    )
                }
            }
            is JournalListAction.OnTopicClick -> {
                val filters = _journalFilter.value
                if (filters.topics.contains(action.topic)) {
                    return
                }

                _journalFilter.update {
                    it.copy(
                        topics = it.topics + action.topic
                    )
                }
            }
            is JournalListAction.OnTogglePlayback -> {
                val journal = action.journal

                if (_currentJournalPlaying.value == null ||
                    _currentJournalPlaying.value?.id.orEmpty() != journal.id
                ) {
                    _currentJournalPlaying.update { journal }
                    _isPlaying.update { true }
                    audioPlayer.play(
                        file = fileManager.getFileFromUri(journal.recordingUri),
                        onComplete = {
                            _isPlaying.update { false }
                        }
                    )
                } else {
                    val shouldPlay = !_isPlaying.value
                    _isPlaying.update { shouldPlay }
                    if (shouldPlay) {
                        audioPlayer.resume()
                    } else {
                        audioPlayer.pause()
                    }
                }
            }
            is JournalListAction.OnRecordPermissionGranted -> {
                _state.update { it.copy(canRecord = action.isGranted) }
            }
            is JournalListAction.OnToggleRecordingBottomSheet -> {
                _state.update { it.copy(isRecordBottomSheetOpened = action.isOpen) }
            }
            JournalListAction.OnToggleRecord -> {
                if (!_state.value.canRecord) {
                    return
                }

                val hasStartedRecording = _state.value.hasStartedRecording

                if (!hasStartedRecording) {
                    _state.update {
                        it.copy(
                            hasStartedRecording = true,
                            isRecording = true
                        )
                    }
                    newJournalId = UUID.randomUUID().toString()
                    audioRecorder.start(newJournalId.orEmpty())
                } else {
                    val shouldRecord = !_state.value.isRecording
                    _state.update { it.copy(isRecording = shouldRecord) }
                    if (shouldRecord) {
                        audioRecorder.resume()
                    } else {
                        audioRecorder.pause()
                    }
                }
            }
            JournalListAction.OnCancelRecordingClick -> {
                audioRecorder.stop(discardFile = true)

                // Reset the state, close the bottom sheet, set hasStartedRecording & isRecording to false
                _state.update {
                    it.copy(
                        isRecordBottomSheetOpened = false,
                        hasStartedRecording = false,
                        isRecording = false
                    )
                }

                // Set the variables to null
                resetNewValues()
            }
            JournalListAction.OnFinishRecordingClick -> {
                viewModelScope.launch {
                    // Get the uri of the created recording file.
                    newJournalUri = audioRecorder.stop().also {
                        if (it.isBlank()) {
                            _eventChannel.send(JournalListEvent.Error(UiText.DynamicString("Error occurred when creating the audio file.")))
                            return@launch
                        }
                    }

                    // Reset the state, close the bottom sheet, set hasStartedRecording & isRecording to false
                    _state.update {
                        it.copy(
                            isRecordBottomSheetOpened = false,
                            hasStartedRecording = false,
                            isRecording = false
                        )
                    }

                    try {
                        requireNotNull(newJournalId)
                        requireNotNull(newJournalUri)

                        _eventChannel.send(
                            JournalListEvent.CreateJournalSuccess(
                                id = newJournalId!!,
                                fileUri = newJournalUri!!
                            )
                        )

                        resetNewValues()
                    } catch (e: Exception) {
                        _eventChannel.send(JournalListEvent.Error(UiText.DynamicString("Something went wrong.")))
                    }
                }
            }
            is JournalListAction.OnSeekCurrentPlayback -> {
                val millis = action.seconds.toDuration(DurationUnit.SECONDS).toInt(DurationUnit.MILLISECONDS)
                audioPlayer.seekTo(millis)
            }
        }
    }
}