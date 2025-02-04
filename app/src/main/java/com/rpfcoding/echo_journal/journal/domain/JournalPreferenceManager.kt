package com.rpfcoding.echo_journal.journal.domain

import kotlinx.coroutines.flow.Flow

interface JournalPreferenceManager {

    fun get(): Flow<JournalPreference>
    suspend fun setDefaultMood(mood: Mood)
    suspend fun setDefaultTopics(topics: Set<String>)
}