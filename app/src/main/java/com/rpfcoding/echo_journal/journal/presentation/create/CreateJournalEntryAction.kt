package com.rpfcoding.echo_journal.journal.presentation.create

sealed interface CreateJournalEntryAction {
    data object OnGoBack: CreateJournalEntryAction
    data object OnSelectMood: CreateJournalEntryAction
    data class OnTitleChange(val value: String): CreateJournalEntryAction
    data class OnDescriptionChange(val value: String): CreateJournalEntryAction
    data object OnToggleAudioPlayer: CreateJournalEntryAction
    data class OnInputTopic(val value: String): CreateJournalEntryAction
    data class OnDeleteTopic(val value: String): CreateJournalEntryAction
    data object OnCancelClick: CreateJournalEntryAction
    data object OnSaveClick: CreateJournalEntryAction
}