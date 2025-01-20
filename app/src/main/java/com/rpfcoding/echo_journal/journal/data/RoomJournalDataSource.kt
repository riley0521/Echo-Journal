package com.rpfcoding.echo_journal.journal.data

import com.rpfcoding.echo_journal.core.database.journal.JournalDao
import com.rpfcoding.echo_journal.core.database.journal.TopicEntity
import com.rpfcoding.echo_journal.journal.data.mapper.toJournal
import com.rpfcoding.echo_journal.journal.data.mapper.toJournalEntity
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.LocalJournalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomJournalDataSource(
    private val journalDao: JournalDao
): LocalJournalDataSource {

    override fun getAll(): Flow<List<Journal>> {
        return journalDao
            .getAll()
            .map { entities ->
                entities.map { it.toJournal() }
            }
    }

    override suspend fun upsert(journal: Journal) {
        journalDao.upsert(journal.toJournalEntity())
    }

    override suspend fun deleteById(id: String) {
        journalDao.deleteById(id)
    }

    override suspend fun deleteAll() {
        journalDao.deleteAll()
    }

    override suspend fun insertTopic(name: String) {
        val newTopic = TopicEntity(name = name)
        journalDao.insertTopic(newTopic)
    }

    override fun getAllTopics(): Flow<Set<String>> {
        return journalDao
            .getAllTopics()
            .map { entities ->
                entities.map { it.name }.toSet()
            }
    }
}