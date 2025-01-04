package com.rpfcoding.echo_journal.journal.domain

import java.time.LocalDateTime
import java.util.UUID

data class Journal(
    val id: String = UUID.randomUUID().toString(),
    val mood: Mood,
    val title: String,
    val description: String,
    val recordingUri: String,
    val dateTimeCreated: LocalDateTime,
    val topics: Set<String>
)