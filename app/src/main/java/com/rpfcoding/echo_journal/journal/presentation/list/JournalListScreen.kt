package com.rpfcoding.echo_journal.journal.presentation.list

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rpfcoding.echo_journal.R
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.core.presentation.ui.ObserveAsEvents
import com.rpfcoding.echo_journal.core.presentation.ui.showToastStr
import com.rpfcoding.echo_journal.core.util.formatLocalDateTimeToHourMinute
import com.rpfcoding.echo_journal.core.util.getDisplayTextByDate
import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.AudioPlayer
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterDropdown
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType
import com.rpfcoding.echo_journal.journal.presentation.components.Topic
import com.rpfcoding.echo_journal.journal.presentation.create.RecordJournalBottomSheet
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodColors
import com.rpfcoding.echo_journal.journal.presentation.util.getResIdByMood
import com.rpfcoding.echo_journal.journal.presentation.util.hasRecordAudioPermission
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import kotlin.random.Random

@Composable
fun JournalListScreenRoot(
    onNavigateToCreateJournal: (id: String, fileUri: String) -> Unit,
    viewModel: JournalListViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is JournalListEvent.CreateJournalSuccess -> {
                onNavigateToCreateJournal(event.id, event.fileUri)
            }
            is JournalListEvent.Error -> {
                context.showToastStr(event.text.asString(context))
            }
        }
    }

    JournalListScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun JournalListScreen(
    state: JournalListState,
    onAction: (JournalListAction) -> Unit
) {

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            onAction(JournalListAction.OnRecordPermissionGranted(isGranted))
        }
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (context.hasRecordAudioPermission()) {
            onAction(JournalListAction.OnRecordPermissionGranted(true))
        }
    }

    fun startRecordingIfPermissionGranted() {
        if (!state.canRecord) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        onAction(JournalListAction.OnToggleRecord)
    }

    if (state.isRecordBottomSheetOpened) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    onAction(JournalListAction.OnToggleRecordingBottomSheet(false))
                }
            },
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            RecordJournalBottomSheet(
                hasStartedRecording = state.hasStartedRecording,
                isPlaying = state.isRecording,
                durationInSeconds = state.durationInSeconds,
                onToggleRecording = ::startRecordingIfPermissionGranted,
                onPausePlay = { onAction(JournalListAction.OnToggleRecord) },
                onCancelRecording = {
                    onAction(JournalListAction.OnCancelRecordingClick)
                },
                onFinishRecording = {
                    onAction(JournalListAction.OnFinishRecordingClick)
                }
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(JournalListAction.OnToggleRecordingBottomSheet(true))
                    startRecordingIfPermissionGranted()
                },
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
            if (state.dateToJournalsMap.isEmpty()) {
                item {
                    EmptyJournalContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .fillParentMaxHeight()
                    )
                }
            } else {
                state.dateToJournalsMap.forEach { (date, journals) ->
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
                                val isFileInPlayback = state.currentFilePlaying == journal.recordingUri
                                JournalItem(
                                    item = journal,
                                    index = index,
                                    isLastItem = index == journals.lastIndex,
                                    isFileInPlayback = isFileInPlayback,
                                    isPlaying = state.isPlaying,
                                    curPlaybackInSeconds = if (isFileInPlayback) {
                                        state.curPlaybackInSeconds
                                    } else 0,
                                    onTopicClick = {
                                        onAction(JournalListAction.OnTopicClick(it))
                                    },
                                    onTogglePlayback = {
                                        onAction(JournalListAction.OnTogglePlayback(journal))
                                    },
                                    onSeekPlayback = {
                                        onAction(JournalListAction.OnSeekCurrentPlayback(it))
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

/**
 * @param index Index of this composable in a list.
 * @param isLastItem if the item is on the last position of the list.
 * @param isFileInPlayback Set to true if the media player is currently playing the recording file of the journal.
 * If true, we can seek the slider to adjust current position.
 * @param onTopicClick Lambda will be triggered if you click topic on the topics of the journal.
 * @param onTogglePlayback Toggle to start/pause the recording file.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JournalItem(
    item: Journal,
    index: Int,
    isLastItem: Boolean,
    isFileInPlayback: Boolean,
    isPlaying: Boolean,
    curPlaybackInSeconds: Long,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTogglePlayback: () -> Unit = {},
    onSeekPlayback: (Int) -> Unit = {}
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
        mutableStateOf(item.description)
    }

    LaunchedEffect(showMore) {
        if (showMore) {
            description = item.description
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
                painter = painterResource(id = getResIdByMood(item.mood)),
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
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = formatLocalDateTimeToHourMinute(item.dateTimeCreated),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            AudioPlayer(
                isPlaying = isPlaying && isFileInPlayback,
                curPlaybackInSeconds = curPlaybackInSeconds,
                maxPlaybackInSeconds = item.maxPlaybackInSeconds,
                moodColors = getMoodColors(item.mood),
                onToggle = { onTogglePlayback() },
                onValueChange = onSeekPlayback,
                enableSlider = isFileInPlayback,
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
                        this.contentDescription = item.description
                    },
                color = MaterialTheme.colorScheme.surfaceVariant,
                onTextLayout = {
                    if (!showMore && it.hasVisualOverflow) {
                        expandable = true
                        description = item
                            .description
                            .substring(0, it.getLineEnd(2, visibleEnd = true))
                    }
                }
            )

            if (item.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.topics.forEach { topic ->
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
        val journals = listOf(
            dummyJournal(
                dateTime = LocalDateTime.now(),
                topics = setOf("Work", "Conundrums")
            ),
            dummyJournal(dateTime = LocalDateTime.now()),
            dummyJournal(dateTime = LocalDateTime.now()),
            dummyJournal(dateTime = LocalDateTime.now()),
            dummyJournal(dateTime = LocalDateTime.now().plusDays(-1)),
            dummyJournal(dateTime = LocalDateTime.now().plusDays(-2)),
        ).groupBy { it.dateTimeCreated.toLocalDate() }
            .toSortedMap(compareByDescending { it })
        var state by remember {
            mutableStateOf(
                JournalListState(
                    dateToJournalsMap = journals
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
                    is JournalListAction.OnTogglePlayback -> {}
                    is JournalListAction.OnRecordPermissionGranted -> {
                        state = state.copy(canRecord = action.isGranted)
                    }
                    is JournalListAction.OnToggleRecordingBottomSheet -> {
                        state = state.copy(isRecordBottomSheetOpened = action.isOpen)
                    }
                    JournalListAction.OnToggleRecord -> {}
                    JournalListAction.OnCancelRecordingClick -> {
                        state = state.copy(isRecordBottomSheetOpened = false)
                    }
                    JournalListAction.OnFinishRecordingClick -> {
                        state = state.copy(isRecordBottomSheetOpened = false)
                    }
                    is JournalListAction.OnSeekCurrentPlayback -> {}
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
            item = dummyJournal(),
            index = 0,
            isLastItem = false,
            isFileInPlayback = false,
            isPlaying = false,
            curPlaybackInSeconds = 0,
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
        maxPlaybackInSeconds = 0,
        dateTimeCreated = dateTime ?: LocalDateTime.now().plusDays(-randomDays),
        topics = topics
    )
}