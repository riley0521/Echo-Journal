package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType

sealed interface JournalListAction {
    data class OnToggleMoodFilter(val filter: JournalFilterType.Moods): JournalListAction
    data class OnToggleTopicFilter(val filter: JournalFilterType.Topics): JournalListAction
    data object OnClearMoodFilter: JournalListAction
    data object OnClearTopicFilter: JournalListAction
    data class OnTopicClick(val topic: String): JournalListAction
    data class OnTogglePlayback(val journal: Journal): JournalListAction
    data object OnOpenCreateRecordingClick: JournalListAction
    data object OnToggleRecord: JournalListAction
    data object OnCancelRecordingClick: JournalListAction
    data object OnFinishRecordingClick: JournalListAction
}