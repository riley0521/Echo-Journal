package com.rpfcoding.echo_journal.core.database.journal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_journals")
data class JournalEntity(
    @PrimaryKey
    val id: String,
    val mood: Int,
    val title: String,
    val description: String,
    val recordingUri: String,
    val maxPlaybackInSeconds: Long,
    val dateTimeCreated: Long,
    val topics: Set<String>
)
