package com.rpfcoding.echo_journal.core.database.journal

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tbl_topics", indices = [Index(value = ["name"], unique = true)])
data class TopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
