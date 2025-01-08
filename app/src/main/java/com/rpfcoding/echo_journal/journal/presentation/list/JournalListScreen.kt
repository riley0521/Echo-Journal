package com.rpfcoding.echo_journal.journal.presentation.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.R
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.core.util.formatLocalDateTimeToHourMinute
import com.rpfcoding.echo_journal.core.util.getDisplayTextByDate
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.AudioPlayer
import com.rpfcoding.echo_journal.journal.presentation.components.Topic
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodColors
import com.rpfcoding.echo_journal.journal.presentation.util.getResIdByMood
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun JournalListScreenRoot() {

}

@Composable
private fun JournalListScreen(
    state: JournalListState,
    onAction: (JournalListAction) -> Unit
) {
    LaunchedEffect(Unit) {
        val f = state.journals.groupBy { it.dateTimeCreated.toLocalDate() }.toSortedMap(compareByDescending { it })
        println(f)
    }

    val dateToJournalsMap = remember(state.journals) {
        state
            .journals
            .groupBy { it.dateTimeCreated.toLocalDate() }
            .toSortedMap(compareByDescending { it })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(
                    bottom = 23.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Your EchoJournal",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
            if (state.journals.isEmpty()) {
                item {
                    EmptyJournalContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight()
                    )
                }
            } else {
                dateToJournalsMap.forEach { (date, journals) ->
                    item {
                        Text(
                            text = getDisplayTextByDate(date).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 28.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        journals.forEachIndexed { index, journal ->
                            JournalItem(
                                journal = journal,
                                index = index,
                                isLastItem = index == journals.lastIndex,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyJournalContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_no_entry),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(34.dp))
        Text(
            text = "No Entries",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Start recording your first Echo",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

// TODO: show more button
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalItem(
    journal: Journal,
    index: Int,
    isLastItem: Boolean,
    modifier: Modifier = Modifier
) {
    var height by remember {
        mutableIntStateOf(0)
    }
    val density = LocalDensity.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (index != 0) {
                VerticalDivider(modifier = Modifier.height(8.dp))
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }

            Image(
                painter = painterResource(id = getResIdByMood(journal.mood)),
                contentDescription = null
            )
            if (!isLastItem) {
                VerticalDivider(
                    modifier = Modifier.height(with(density) { height.toDp() })
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(
                    vertical = 12.dp,
                    horizontal = 14.dp
                )
                .onGloballyPositioned {
                    height = it.size.height
                }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = journal.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = formatLocalDateTimeToHourMinute(journal.dateTimeCreated),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            AudioPlayer(
                isPlaying = false,
                curPlaybackInSeconds = 92,
                maxPlaybackInSeconds = 184,
                moodColors = getMoodColors(journal.mood),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = journal.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            if (journal.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    journal.topics.forEach { topic ->
                        Topic(
                            text = topic
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun JournalListScreenPreview() {
    EchoJournalTheme {
        JournalListScreen(
            state = JournalListState(
                journals = listOf(
                    dummyJournal(dateTime = LocalDateTime.now(), topics = setOf("Work", "Conundrums")),
                    dummyJournal(dateTime = LocalDateTime.now()),
                    dummyJournal(dateTime = LocalDateTime.now().plusDays(-1)),
                    dummyJournal(dateTime = LocalDateTime.now().plusDays(-1)),
                    dummyJournal(dateTime = LocalDateTime.now().plusDays(-2)),
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
private fun EmptyJournalContentPreview() {
    EchoJournalTheme {
        EmptyJournalContent(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

@Preview
@Composable
private fun JournalItemPreview() {
    EchoJournalTheme {
        JournalItem(
            journal = dummyJournal(),
            index = 0,
            isLastItem = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

private fun dummyJournal(dateTime: LocalDateTime? = null, topics: Set<String> = emptySet()): Journal {
    val randomDays = Random.nextInt(1, 8).toLong()

    return Journal(
        mood = Mood.entries.random(),
        title = "My Entry",
        description = LoremIpsum(69).values.joinToString(" "),
        recordingUri = "",
        dateTimeCreated = dateTime ?: LocalDateTime.now().plusDays(-randomDays),
        topics = topics
    )
}