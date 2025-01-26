package com.rpfcoding.echo_journal.core.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.core.net.toUri
import com.rpfcoding.echo_journal.core.domain.audio.AudioRecorder
import com.rpfcoding.echo_journal.core.domain.audio.FileParent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class AndroidAudioRecorder(
    private val context: Context,
    private val applicationScope: CoroutineScope
): AudioRecorder {

    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    private var recordingJob: Job? = null
    private val _durationInMillis = MutableStateFlow<Long>(0)
    override val durationInMillis: StateFlow<Long> = _durationInMillis

    private fun createRecorder(): MediaRecorder {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    override fun start(fileName: String, fileParent: FileParent) {
        val parent = when(fileParent) {
            FileParent.Cache -> context.cacheDir
            is FileParent.Custom -> fileParent.value
        }
        if (!parent.isDirectory) {
            throw Exception("Custom fileParent is not a folder!")
        }

        outputFile = File(parent, "${fileName}.mp3")

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(outputFile!!.absolutePath)

            prepare()
            start()

            recorder = this
            updateDuration()
        }
    }

    private fun updateDuration() {
        recordingJob?.cancel()
        recordingJob = applicationScope.launch {
            while(true) {
                delay(100)
                _durationInMillis.update { it + 100 }
            }
        }
    }

    override fun resume() {
        recorder?.resume()
        updateDuration()
    }

    private fun resetJob() {
        recordingJob?.cancel()
        recordingJob = null
    }

    override fun pause() {
        recorder?.pause()
        resetJob()
    }

    override fun stop(discardFile: Boolean): String {
        if (discardFile) {
            outputFile?.delete()
        }

        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
        resetJob()
        _durationInMillis.update { 0L }

        return outputFile?.toUri()?.toString().orEmpty()
    }
}