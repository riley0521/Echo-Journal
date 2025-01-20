package com.rpfcoding.echo_journal.journal.domain

import kotlinx.coroutines.flow.Flow

interface LocalJournalDataSource {

    fun getAll(): Flow<List<Journal>>

    suspend fun upsert(journal: Journal)
    suspend fun deleteById(id: String)
    suspend fun deleteAll()
    suspend fun insertTopic(name: String)
    fun getAllTopics(): Flow<Set<String>>
}