package com.rpfcoding.echo_journal.journal.domain

data class JournalPreference(
    val selectedMood: Mood? = null,
    val selectedTopics: Set<String> = emptySet()
)
