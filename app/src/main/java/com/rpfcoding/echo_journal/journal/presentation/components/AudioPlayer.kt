package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.core.util.formatSecondsToMinSecond
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodColors
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

data class MoodColors(
    val primary: Color,
    val secondary: Color,
    val container: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayer(
    isPlaying: Boolean,
    curPlaybackInSeconds: Long,
    maxPlaybackInSeconds: Long,
    moodColors: MoodColors,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit = {},
    playerRadius: Dp = 999.dp
) {
    var sliderValue by remember(curPlaybackInSeconds) {
        mutableFloatStateOf(curPlaybackInSeconds.toFloat())
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(playerRadius))
            .background(moodColors.container)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onToggle,
            modifier = Modifier
                .size(32.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(playerRadius))
                .clip(RoundedCornerShape(playerRadius))
                .background(Color.White)

        ) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = null,
                tint = moodColors.primary
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                onValueChange(sliderValue.roundToInt())
            },
            valueRange = 0f..(maxPlaybackInSeconds.toFloat()),
            enabled = false, // TODO: Later in dev, we can implement seeking timestamp
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = remember { MutableInteractionSource() },
                    colors = SliderDefaults.colors(
                        thumbColor = moodColors.primary
                    ),
                    modifier = Modifier.clip(CircleShape)
                )
            },
            track = {
                SliderDefaults.Track(
                    sliderState = it,
                    modifier = Modifier
                        .height(4.dp),
                    drawTick = { offset, color -> },
                    drawStopIndicator = {},
                    thumbTrackGapSize = 0.dp,
                    trackInsideCornerSize = 0.dp,
                    colors = SliderDefaults.colors(
                        activeTrackColor = moodColors.primary,
                        inactiveTrackColor = moodColors.secondary
                    )
                )
            },
            modifier = Modifier
                .weight(1f)
                .heightIn(max = 8.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "${formatSecondsToMinSecond(curPlaybackInSeconds)}/${formatSecondsToMinSecond(maxPlaybackInSeconds)}",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

@Preview
@Composable
private fun AudioPlayerPreview() {
    EchoJournalTheme {

        var curPlaybackInSeconds by remember {
            mutableLongStateOf(90)
        }

        val maxPlaybackInSeconds = remember {
            184L
        }

        var isPlaying by remember {
            mutableStateOf(false)
        }

        LaunchedEffect(isPlaying) {
            if (isPlaying) {
                while (curPlaybackInSeconds < maxPlaybackInSeconds) {
                    delay(1000L)
                    curPlaybackInSeconds++
                }
            }
        }

        LaunchedEffect(curPlaybackInSeconds) {
            if (curPlaybackInSeconds == maxPlaybackInSeconds) {
                isPlaying = false
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Mood.entries.forEach { mood ->
                AudioPlayer(
                    isPlaying = isPlaying,
                    curPlaybackInSeconds = curPlaybackInSeconds,
                    maxPlaybackInSeconds = maxPlaybackInSeconds,
                    moodColors = getMoodColors(mood),
                    onToggle = {
                        isPlaying = !isPlaying
                    },
                    onValueChange = {
                        curPlaybackInSeconds = it.toLong()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}