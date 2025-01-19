package com.rpfcoding.echo_journal.core.audio.domain

import kotlinx.coroutines.flow.StateFlow
import java.io.File

sealed interface FileParent {
    data object Cache: FileParent
    data class Custom(val value: File): FileParent
}

interface AudioRecorder {

    val durationInMillis: StateFlow<Long>

    /**
     * @param fileName Should be a file name without extension e.g. 'my_sad_audio_24'
     * @param fileParent Parent file directory, default is to store it in cache.
     */
    fun start(fileName: String, fileParent: FileParent = FileParent.Cache)
    fun resume()
    fun pause()

    /**
     * @return Uri of the created audio file.
     */
    fun stop(): String
}