package com.rpfcoding.echo_journal.core.domain.file

import java.io.File

interface FileManager {

    /**
     * @param file The audio file.
     * @return duration in seconds or -1 if the file is invalid or not found.
     */
    fun getDurationOfAudioFile(file: File): Long

    fun getFileFromUri(value: String): File
}