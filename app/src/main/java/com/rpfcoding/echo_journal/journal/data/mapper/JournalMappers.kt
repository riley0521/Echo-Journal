package com.rpfcoding.echo_journal.journal.data.mapper

import com.rpfcoding.echo_journal.core.database.journal.JournalEntity
import com.rpfcoding.echo_journal.core.util.toLocalDateTime
import com.rpfcoding.echo_journal.core.util.toMillis
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.Mood

fun Journal.toJournalEntity(): JournalEntity {
    return JournalEntity(
        id = id,
        mood = mood.ordinal,
        title = title,
        description = description,
        recordingUri = recordingUri,
        maxPlaybackInSeconds = maxPlaybackInSeconds,
        dateTimeCreated = dateTimeCreated.toMillis(),
        topics = topics
    )
}

fun JournalEntity.toJournal(): Journal {
    return Journal(
        id = id,
        mood = Mood.entries[mood],
        title = title,
        description = description,
        recordingUri = recordingUri,
        maxPlaybackInSeconds = maxPlaybackInSeconds,
        dateTimeCreated = dateTimeCreated.toLocalDateTime(),
        topics = topics
    )
}