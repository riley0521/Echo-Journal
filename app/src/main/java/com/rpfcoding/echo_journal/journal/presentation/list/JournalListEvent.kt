package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.core.presentation.ui.UiText

sealed interface JournalListEvent {
    data class CreateJournalSuccess(val id: String, val fileUri: String): JournalListEvent
    data class Error(val text: UiText): JournalListEvent
}