package com.rpfcoding.echo_journal.journal.presentation.di

import com.rpfcoding.echo_journal.journal.presentation.create.CreateJournalEntryViewModel
import com.rpfcoding.echo_journal.journal.presentation.list.JournalListViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val journalPresentationModule = module {
    viewModelOf(::JournalListViewModel)
    viewModelOf(::CreateJournalEntryViewModel)
}