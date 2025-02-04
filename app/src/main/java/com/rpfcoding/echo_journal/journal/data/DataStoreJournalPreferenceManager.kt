package com.rpfcoding.echo_journal.journal.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rpfcoding.echo_journal.journal.domain.JournalPreference
import com.rpfcoding.echo_journal.journal.domain.JournalPreferenceManager
import com.rpfcoding.echo_journal.journal.domain.Mood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "journal_pref")

class DataStoreJournalPreferenceManager(
    private val context: Context
): JournalPreferenceManager {

    companion object {
        private val MOOD_INDEX_KEY = intPreferencesKey("MOOD_INDEX_KEY")
        private val TOPICS_JSON_KEY = stringPreferencesKey("TOPICS_JSON_KEY")
    }

    override fun get(): Flow<JournalPreference> {
        return context
            .dataStore
            .data
            .map { preferences ->
                val topics: Set<String> = preferences[TOPICS_JSON_KEY]?.let {
                    Json.decodeFromString(it)
                } ?: emptySet()

                JournalPreference(
                    selectedMood = preferences[MOOD_INDEX_KEY]?.let {
                        Mood.entries[it]
                    },
                    selectedTopics = topics
                )
            }
    }

    override suspend fun setDefaultMood(mood: Mood) {
        context.dataStore.edit {
            it[MOOD_INDEX_KEY] = mood.ordinal
        }
    }

    override suspend fun setDefaultTopics(topics: Set<String>) {
        context.dataStore.edit {
            it[TOPICS_JSON_KEY] = Json.encodeToString(topics)
        }
    }
}