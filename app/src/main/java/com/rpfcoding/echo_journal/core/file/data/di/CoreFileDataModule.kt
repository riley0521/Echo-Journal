package com.rpfcoding.echo_journal.core.file.data.di

import com.rpfcoding.echo_journal.core.file.data.AndroidFileManager
import com.rpfcoding.echo_journal.core.file.domain.FileManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreFileDataModule = module {
    singleOf(::AndroidFileManager).bind<FileManager>()
}