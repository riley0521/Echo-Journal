package com.rpfcoding.echo_journal.journal.presentation.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterDropdown
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType
import com.rpfcoding.echo_journal.journal.presentation.components.Topic
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodColors
import com.rpfcoding.echo_journal.journal.presentation.util.getResIdByMood
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun JournalListScreenRoot() {

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalListScreen(
    state: JournalListState,
    onAction: (JournalListAction) -> Unit
) {
    
    // TODO: Remove this and dateToJournalsMap, this should be sorted in viewModel
    LaunchedEffect(Unit) {
        val f = state
            .journals
            .groupBy { it.dateTimeCreated.toLocalDate() }
            .toSortedMap(compareByDescending { it })
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
            item {
                Spacer(modifier = Modifier.height(16.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val shouldAddModifier = state.filteredTopics.getSelectedCount() >= 2 ||
                            state.filteredMoods.getSelectedCount() >= 2
                    val widthModifier = if (shouldAddModifier) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                    }

                    JournalFilterDropdown(
                        title = "All Moods",
                        filterType = state.filteredMoods,
                        onToggle = { filterType ->
                            onAction(JournalListAction.OnToggleMoodFilter(filterType))
                        },
                        onClearFilter = {
                            onAction(JournalListAction.OnClearMoodFilter)
                        },
                        modifier = widthModifier
                    )
                    if (!shouldAddModifier) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    JournalFilterDropdown(
                        title = "All Topics",
                        filterType = state.filteredTopics,
                        onToggle = { filterType ->
                            onAction(JournalListAction.OnToggleTopicFilter(filterType))
                        },
                        onClearFilter = {
                            onAction(JournalListAction.OnClearTopicFilter)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
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
                            modifier = Modifier.padding(top = 28.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .semantics {
                                    this.collectionInfo = CollectionInfo(-1, 1)
                                }
                        ) {
                            journals.forEachIndexed { index, journal ->
                                JournalItem(
                                    journal = journal,
                                    index = index,
                                    isLastItem = index == journals.lastIndex,
                                    onTopicClick = {
                                        onAction(JournalListAction.OnTopicClick(it))
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalItem(
    journal: Journal,
    index: Int,
    isLastItem: Boolean,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTogglePlayback: (Journal) -> Unit = {}
) {
    var height by remember {
        mutableIntStateOf(0)
    }
    val density = LocalDensity.current

    var expandable by remember {
        mutableStateOf(false)
    }
    var showMore by remember {
        mutableStateOf(false)
    }
    var description by remember {
        mutableStateOf(journal.description)
    }

    LaunchedEffect(showMore) {
        if (showMore) {
            description = journal.description
        }
    }

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

            // TODO: Create JournalUi to store the state.
            AudioPlayer(
                isPlaying = false,
                curPlaybackInSeconds = 3945,
                maxPlaybackInSeconds = (1.hours + 10.minutes + 11.seconds).inWholeSeconds,
                moodColors = getMoodColors(journal.mood),
                onToggle = { onTogglePlayback(journal) },
                onValueChange = {}, // TODO
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            val isClickable = expandable && !showMore
            val annotatedText = getDescription(description, isClickable)
            Text(
                text = annotatedText,
                maxLines = if (showMore) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .clickable(
                        enabled = isClickable
                    ) {
                        showMore = true
                    }
                    .semantics {
                        this.contentDescription = journal.description
                    },
                color = MaterialTheme.colorScheme.surfaceVariant,
                onTextLayout = {
                    if (!showMore && it.hasVisualOverflow) {
                        expandable = true
                        description = journal.description.substring(0, it.getLineEnd(2, visibleEnd = true))
                    }
                }
            )

            if (journal.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    journal.topics.forEach { topic ->
                        Topic(
                            text = topic,
                            onClick = { onTopicClick(topic) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getDescription(value: String, expandable: Boolean): AnnotatedString {
    val showMoreStr = "... Show more"
    return buildAnnotatedString {
        if (expandable && value.length > showMoreStr.length) {
            append(value.substring(0, value.length - showMoreStr.length))
        } else {
            append(value)
        }
        if (expandable) {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append(showMoreStr)
            }
        }
    }
}

@Preview
@Composable
private fun JournalListScreenPreview() {
    EchoJournalTheme {
        var state by remember {
            mutableStateOf(
                JournalListState(
                    journals = listOf(
                        dummyJournal(
                            dateTime = LocalDateTime.now(),
                            topics = setOf("Work", "Conundrums")
                        ),
                        dummyJournal(dateTime = LocalDateTime.now(), wordCount = 12),
                        dummyJournal(dateTime = LocalDateTime.now().plusDays(-1)),
                        dummyJournal(dateTime = LocalDateTime.now().plusDays(-1)),
                        dummyJournal(dateTime = LocalDateTime.now().plusDays(-2)),
                    )
                )
            )
        }

        JournalListScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is JournalListAction.OnToggleMoodFilter -> {
                        state = state.copy(filteredMoods = action.filter)
                    }
                    is JournalListAction.OnToggleTopicFilter -> {
                        state = state.copy(filteredTopics = action.filter)
                    }
                    is JournalListAction.OnTopicClick -> {
                        val updatedFilteredTopics = state.filteredTopics.topics.map {
                            if (it.name == action.topic && !it.isSelected) {
                                it.copy(isSelected = true)
                            } else it
                        }.toSet()
                        state = state.copy(filteredTopics = JournalFilterType.Topics(updatedFilteredTopics))
                    }
                    JournalListAction.OnClearMoodFilter -> {
                        val moods = state.filteredMoods.moods.map { it.copy(isSelected = false) }.toSet()
                        state = state.copy(filteredMoods = JournalFilterType.Moods(moods))
                    }
                    JournalListAction.OnClearTopicFilter -> {
                        val topics = state.filteredTopics.topics.map { it.copy(isSelected = false) }.toSet()
                        state = state.copy(filteredTopics = JournalFilterType.Topics(topics))
                    }
                }
            }
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
            onTopicClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        )
    }
}

private fun dummyJournal(
    dateTime: LocalDateTime? = null,
    topics: Set<String> = emptySet(),
    wordCount: Int = 69
): Journal {
    val randomDays = Random.nextInt(1, 8).toLong()

    return Journal(
        mood = Mood.entries.random(),
        title = "My Entry",
        description = LoremIpsum(wordCount).values.joinToString(" "),
        recordingUri = "",
        dateTimeCreated = dateTime ?: LocalDateTime.now().plusDays(-randomDays),
        topics = topics
    )
}