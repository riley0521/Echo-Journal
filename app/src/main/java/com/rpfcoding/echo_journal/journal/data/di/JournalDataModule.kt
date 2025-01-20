package com.rpfcoding.echo_journal.journal.data.di

import com.rpfcoding.echo_journal.journal.data.RoomJournalDataSource
import com.rpfcoding.echo_journal.journal.domain.LocalJournalDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val journalDataModule = module {
    singleOf(::RoomJournalDataSource).bind<LocalJournalDataSource>()
}