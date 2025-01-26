package com.rpfcoding.echo_journal.journal.presentation.create

import com.rpfcoding.echo_journal.core.presentation.ui.UiText

sealed interface CreateJournalEntryEvent {
    data object Success: CreateJournalEntryEvent
    data class Error(val error: UiText): CreateJournalEntryEvent
    data object NavigateBack: CreateJournalEntryEvent
}