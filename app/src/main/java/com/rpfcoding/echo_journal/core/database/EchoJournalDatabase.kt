package com.rpfcoding.echo_journal.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rpfcoding.echo_journal.core.database.journal.JournalDao
import com.rpfcoding.echo_journal.core.database.journal.JournalEntity
import com.rpfcoding.echo_journal.core.database.journal.TopicEntity

@Database(
    entities = [JournalEntity::class, TopicEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class EchoJournalDatabase: RoomDatabase() {

    abstract val journalDao: JournalDao
}