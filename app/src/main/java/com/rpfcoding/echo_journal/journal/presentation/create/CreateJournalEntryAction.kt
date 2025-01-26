package com.rpfcoding.echo_journal.journal.presentation.create

import com.rpfcoding.echo_journal.journal.domain.Mood

sealed interface CreateJournalEntryAction {
    data class OnToggleSelectMoodBottomSheet(val isOpen: Boolean): CreateJournalEntryAction
    data class OnSelectMood(val mood: Mood): CreateJournalEntryAction
    data class OnTitleChange(val value: String): CreateJournalEntryAction
    data class OnDescriptionChange(val value: String): CreateJournalEntryAction
    data object OnToggleAudioPlayer: CreateJournalEntryAction
    data class OnInputTopic(val value: String): CreateJournalEntryAction
    data class OnDeleteTopic(val value: String): CreateJournalEntryAction
    data class OnSelectTopic(val value: String): CreateJournalEntryAction
    data object OnAddNewTopic: CreateJournalEntryAction
    data class OnToggleCancelDialog(val isOpen: Boolean): CreateJournalEntryAction
    data object OnCancelCreateJournalEntry: CreateJournalEntryAction
    data object OnSaveClick: CreateJournalEntryAction
    data class OnSeekCurrentPlayback(val seconds: Int): CreateJournalEntryAction
}