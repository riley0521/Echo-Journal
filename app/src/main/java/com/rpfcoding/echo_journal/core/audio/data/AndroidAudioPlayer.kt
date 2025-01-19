package com.rpfcoding.echo_journal.core.audio.data

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import com.rpfcoding.echo_journal.core.audio.domain.AudioPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AndroidAudioPlayer(
    private val context: Context,
    private val applicationScope: CoroutineScope
): AudioPlayer {

    private val _curPlaybackInSeconds = MutableStateFlow<Long>(0)
    override val curPlaybackInSeconds: StateFlow<Long> = _curPlaybackInSeconds

    private var player: MediaPlayer? = null
    private var currentAudioUri: Uri? = null
    private var playbackJob: Job? = null

    override fun play(file: File) {

        val audioUri = file.toUri()
        if (currentAudioUri != null) {
            resetAllState()
        }

        MediaPlayer.create(context, audioUri).apply {
            player = this
            currentAudioUri = audioUri
            start()

            updatePlayback()
        }
    }

    private fun resetAllState() {
        player?.stop()
        player?.release()
        player = null
        currentAudioUri = null
        resetJob()
        _curPlaybackInSeconds.update { 0L }
    }

    private fun updatePlayback() {
        playbackJob?.cancel()
        playbackJob = applicationScope.launch {
            while(true) {
                _curPlaybackInSeconds.update {
                    player!!.currentPosition.toDuration(DurationUnit.MILLISECONDS).toLong(DurationUnit.SECONDS)
                }
                delay(100.milliseconds)
            }
        }
    }

    private fun resetJob() {
        playbackJob?.cancel()
        playbackJob = null
    }

    override fun pause() {
        player?.pause()
        resetJob()
    }

    override fun resume() {
        player?.start()
        updatePlayback()
    }
}