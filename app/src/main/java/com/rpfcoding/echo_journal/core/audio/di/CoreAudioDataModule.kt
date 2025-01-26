package com.rpfcoding.echo_journal.core.audio.di

import com.rpfcoding.echo_journal.core.audio.AndroidAudioPlayer
import com.rpfcoding.echo_journal.core.audio.AndroidAudioRecorder
import com.rpfcoding.echo_journal.core.domain.audio.AudioPlayer
import com.rpfcoding.echo_journal.core.domain.audio.AudioRecorder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreAudioDataModule = module {
    singleOf(::AndroidAudioPlayer).bind<AudioPlayer>()
    singleOf(::AndroidAudioRecorder).bind<AudioRecorder>()
}