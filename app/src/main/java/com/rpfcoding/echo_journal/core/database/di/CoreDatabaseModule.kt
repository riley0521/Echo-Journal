package com.rpfcoding.echo_journal.core.database.di

import androidx.room.Room
import com.rpfcoding.echo_journal.core.database.EchoJournalDatabase
import com.rpfcoding.echo_journal.core.database.journal.JournalDao
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val coreDatabaseModule = module {
    single<EchoJournalDatabase> {
        Room.databaseBuilder(
            context = androidApplication(),
            klass = EchoJournalDatabase::class.java,
            name = "journal.db"
        ).build()
    }
    single<JournalDao> {
        get<EchoJournalDatabase>().journalDao
    }
}