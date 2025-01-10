package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType

sealed interface JournalListAction {
    data class OnToggleMoodFilter(val filter: JournalFilterType.Moods): JournalListAction
    data class OnToggleTopicFilter(val filter: JournalFilterType.Topics): JournalListAction
}