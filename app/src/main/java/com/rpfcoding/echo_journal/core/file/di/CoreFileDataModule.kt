package com.rpfcoding.echo_journal.core.file.di

import com.rpfcoding.echo_journal.core.domain.file.FileManager
import com.rpfcoding.echo_journal.core.file.AndroidFileManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreFileDataModule = module {
    singleOf(::AndroidFileManager).bind<FileManager>()
}