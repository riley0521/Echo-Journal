package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType
import com.rpfcoding.echo_journal.journal.presentation.components.TopicUi
import com.rpfcoding.echo_journal.journal.presentation.components.getMoodsFilterType
import java.time.LocalDate

data class JournalListState(
    val dateToJournalsMap: Map<LocalDate, List<Journal>> = emptyMap(),
    val canRecord: Boolean = false,
    val isRecordBottomSheetOpened: Boolean = false,
    val isAlternativeRecordingType: Boolean = false,
    val hasStartedRecording: Boolean = false,
    val isRecording: Boolean = false,
    val durationInSeconds: Long = 0L,
    val currentFilePlaying: String? = null,
    val isPlaying: Boolean = false,
    val curPlaybackInSeconds: Long = 0,
    val filteredTopics: JournalFilterType.Topics = JournalFilterType.Topics(emptySet()),
    val filteredMoods: JournalFilterType.Moods = getMoodsFilterType()
)

val dummyTopics = setOf(
    TopicUi("Work", false),
    TopicUi("Conundrums", false),
    TopicUi("Gym", false),
    TopicUi("Love", false)
)
