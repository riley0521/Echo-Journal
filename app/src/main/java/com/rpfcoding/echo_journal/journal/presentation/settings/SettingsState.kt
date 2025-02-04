package com.rpfcoding.echo_journal.journal.presentation.settings

import com.rpfcoding.echo_journal.journal.domain.Mood

data class SettingsState(
    val selectedMood: Mood? = null,
    val inputTopic: String = "",
    val isNewTopic: Boolean = false,
    val isTopicFieldFocused: Boolean = false,
    val unselectedTopics: Set<String> = emptySet(),
    val selectedTopics: Set<String> = emptySet()
)
