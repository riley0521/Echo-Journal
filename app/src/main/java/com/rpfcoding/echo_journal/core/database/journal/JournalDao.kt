package com.rpfcoding.echo_journal.core.database.journal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM tbl_journals")
    fun getAll(): Flow<List<JournalEntity>>

    @Upsert
    suspend fun upsert(journal: JournalEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTopic(topic: TopicEntity)

    @Query("SELECT * FROM tbl_topics")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("DELETE FROM tbl_journals WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM tbl_journals")
    suspend fun deleteAll()
}