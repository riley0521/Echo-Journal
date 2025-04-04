package com.rpfcoding.echo_journal.core.domain.audio

import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface AudioPlayer {
    val curPlaybackInSeconds: StateFlow<Long>

    fun play(file: File, onComplete: () -> Unit, shouldPlayImmediately: Boolean = true)
    fun pause()
    fun resume()
    fun stopAndResetPlayer()
    fun seekTo(millis: Int)
}