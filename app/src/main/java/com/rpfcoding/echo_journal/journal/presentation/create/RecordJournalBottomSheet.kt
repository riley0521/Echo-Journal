package com.rpfcoding.echo_journal.journal.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.core.util.formatSecondsToHourMinuteSecond
import com.rpfcoding.echo_journal.journal.presentation.components.BottomSheetWithHeaderAndFooter
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RecordJournalBottomSheet(
    hasStartedRecording: Boolean,
    isPlaying: Boolean,
    durationInSeconds: Long,
    onFinishRecording: () -> Unit,
    onPausePlay: () -> Unit,
    onCancelRecording: () -> Unit
) {
    val playButtonInnerSize = 72.dp
    val playButtonMiddleSize = (playButtonInnerSize.value + (playButtonInnerSize.value * 0.35)).roundToInt().dp
    val playButtonOuterSize = (playButtonInnerSize.value + (playButtonInnerSize.value * 0.6)).roundToInt().dp

    BottomSheetWithHeaderAndFooter {
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = if (isPlaying) {
                "Recording your memories..."
            } else {
                "Recording paused"
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatSecondsToHourMinuteSecond(durationInSeconds),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable {
                        onCancelRecording()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel recording",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(24.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (hasStartedRecording) {
                    Box(
                        modifier = Modifier
                            .size(playButtonOuterSize)
                            .clip(RoundedCornerShape(133.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    )

                    Box(
                        modifier = Modifier
                            .size(playButtonMiddleSize)
                            .clip(RoundedCornerShape(133.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(playButtonInnerSize)
                        .clip(RoundedCornerShape(133.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            onFinishRecording()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasStartedRecording) Icons.Default.Check else Icons.Default.Mic,
                        contentDescription = "Finish recording",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {
                        onPausePlay()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Preview
@Composable
private fun RecordJournalBottomSheetPreview() {
    EchoJournalTheme {
        RecordJournalBottomSheet(
            hasStartedRecording = true,
            isPlaying = true,
            durationInSeconds = (3.hours + 22.minutes + 24.seconds).inWholeSeconds,
            onFinishRecording = {},
            onPausePlay = {},
            onCancelRecording = {}
        )
    }
}