package com.rpfcoding.echo_journal.core.audio.data

import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.rpfcoding.echo_journal.core.audio.domain.AudioRecorder
import com.rpfcoding.echo_journal.core.audio.domain.FileParent
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
    private var outputUri: Uri? = null

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
        val outputFile = File(parent, "${fileName}.mp3")

        createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128_000)
            setAudioSamplingRate(44_100)
            setOutputFile(outputFile.absolutePath)

            prepare()
            start()

            recorder = this
            outputUri = outputFile.toUri()
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

    override fun stop(): String {
        recorder?.stop()
        recorder?.reset()
        recorder?.release()
        recorder = null
        resetJob()
        _durationInMillis.update { 0L }

        return outputUri?.toString() ?: throw Exception("There is no created recording.")
    }
}