package com.rpfcoding.echo_journal.core.audio.data.di

import com.rpfcoding.echo_journal.core.audio.data.AndroidAudioPlayer
import com.rpfcoding.echo_journal.core.audio.data.AndroidAudioRecorder
import com.rpfcoding.echo_journal.core.audio.domain.AudioPlayer
import com.rpfcoding.echo_journal.core.audio.domain.AudioRecorder
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreAudioDataModule = module {
    singleOf(::AndroidAudioPlayer).bind<AudioPlayer>()
    singleOf(::AndroidAudioRecorder).bind<AudioRecorder>()
}