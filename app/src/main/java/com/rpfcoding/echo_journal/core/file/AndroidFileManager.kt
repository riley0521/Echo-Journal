package com.rpfcoding.echo_journal.core.file

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.rpfcoding.echo_journal.core.domain.file.FileManager
import java.io.File
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AndroidFileManager(
    private val context: Context
): FileManager {

    override fun getDurationOfAudioFile(file: File): Long {
        val mmr = MediaMetadataRetriever().apply {
            setDataSource(context, file.toUri())
        }
        val duration = mmr
            .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            ?.toIntOrNull()
            ?.toDuration(DurationUnit.MILLISECONDS)
            ?.toLong(DurationUnit.SECONDS)
        return duration ?: -1L
    }

    override fun getFileFromUri(value: String): File {
        return Uri.parse(value).toFile()
    }
}