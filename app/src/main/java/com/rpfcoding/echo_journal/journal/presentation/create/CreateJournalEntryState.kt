package com.rpfcoding.echo_journal.journal.presentation.create

import com.rpfcoding.echo_journal.journal.domain.Mood

data class CreateJournalEntryState(
    val title: String = "",
    val description: String = "",
    val selectedMood: Mood? = null,
    val isSelectMoodBottomSheetOpened: Boolean = false,
    val canSave: Boolean = false,
    val isPlaying: Boolean = false,
    val curPlaybackInSeconds: Long = 0,
    val maxPlaybackInSeconds: Long = 0,
    val inputTopic: String = "",
    val isCancelCreateJournalEntryDialogOpened: Boolean = false,
    val allTopics: Set<String> = emptySet(), // Should not contain topics that are already selected.
    val selectedTopics: Set<String> = emptySet(),
)
