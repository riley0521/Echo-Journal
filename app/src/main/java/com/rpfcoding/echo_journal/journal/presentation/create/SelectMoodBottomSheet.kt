package com.rpfcoding.echo_journal.journal.presentation.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.BottomSheetWithHeaderAndFooter
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodOutlined
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodUiByMood
import com.rpfcoding.echo_journal.journal.presentation.util.getResIdByMood

@Composable
fun SelectMoodBottomSheet(
    initialMood: Mood?,
    onCancel: () -> Unit,
    onConfirm: (Mood) -> Unit,
) {
    var selectedMood by remember(initialMood) {
        mutableStateOf(initialMood)
    }

    BottomSheetWithHeaderAndFooter(hideFooter = true) {
        Text(
            text = "How are you doing?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Mood.entries.forEach {
                MoodItem(
                    mood = it,
                    isSelected = selectedMood == it,
                    onClick = {
                        selectedMood = it
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = "Cancel"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    onConfirm(selectedMood!!)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = Color.White
                ),
                modifier = Modifier.weight(1f),
                enabled = selectedMood != null
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Confirm"
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MoodItem(
    mood: Mood,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = getMoodUiByMood(mood).name
    val resId = if (isSelected) getResIdByMood(mood) else getMoodOutlined(mood)

    Column(
        modifier = modifier
            .clickable {
                onClick()
            },
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.surfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 40.dp)
        )
    }
}

@Preview
@Composable
private fun SelectMoodBottomSheetPreview() {
    EchoJournalTheme {
        SelectMoodBottomSheet(
            initialMood = null,
            onCancel = {},
            onConfirm = {}
        )
    }
}