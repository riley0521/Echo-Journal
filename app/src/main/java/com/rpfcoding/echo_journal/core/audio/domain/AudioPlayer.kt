package com.rpfcoding.echo_journal.core.audio.domain

import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface AudioPlayer {
    val curPlaybackInSeconds: StateFlow<Long>

    fun play(file: File)
    fun pause()
    fun resume()
}