package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme

@Composable
fun Topic(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xfff2f2f7),
    hashtagColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#",
            style = MaterialTheme.typography.labelSmall,
            color = hashtagColor.copy(alpha = 0.5f),
            modifier = Modifier.semantics {
                // For accessibility, we don't want the talkback to read this.
                this.invisibleToUser()
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = hashtagColor
        )
    }
}

@Preview
@Composable
private fun TopicPreview() {
    EchoJournalTheme {
        Topic(
            text = "Work"
        )
    }
}