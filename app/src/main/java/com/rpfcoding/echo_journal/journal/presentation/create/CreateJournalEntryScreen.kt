package com.rpfcoding.echo_journal.journal.presentation.create

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.core.presentation.ui.ObserveAsEvents
import com.rpfcoding.echo_journal.core.presentation.ui.showToastStr
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.AudioPlayer
import com.rpfcoding.echo_journal.journal.presentation.components.Topic
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodColors
import com.rpfcoding.echo_journal.journal.presentation.util.getResIdByMood
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateJournalEntryScreenRoot(
    onNavigateBack: () -> Unit,
    viewModel: CreateJournalEntryViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CreateJournalEntryEvent.Error -> {
                context.showToastStr(event.error.asString(context))
            }
            CreateJournalEntryEvent.NavigateBack -> {
                onNavigateBack()
            }
            CreateJournalEntryEvent.Success -> {
                context.showToastStr("Journal successfully created!")
                onNavigateBack()
            }
        }
    }

    CreateJournalEntryScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CreateJournalEntryScreen(
    state: CreateJournalEntryState,
    onAction: (CreateJournalEntryAction) -> Unit
) {
    val color = Color(0xffc1c3ce)
    var topicsHeight by remember {
        mutableIntStateOf(0)
    }
    var isTopicsFieldFocused by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun hideBottomSheet() {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            onAction(CreateJournalEntryAction.OnToggleSelectMoodBottomSheet(false))
        }
    }

    if (state.isSelectMoodBottomSheetOpened) {
        ModalBottomSheet(
            onDismissRequest = ::hideBottomSheet,
            sheetState = sheetState,
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            SelectMoodBottomSheet(
                initialMood = state.selectedMood,
                onCancel = ::hideBottomSheet,
                onConfirm = {
                    onAction(CreateJournalEntryAction.OnSelectMood(it))
                    hideBottomSheet()
                }
            )
        }
    }

    if (state.isCancelCreateJournalEntryDialogOpened) {
        Dialog(
            onDismissRequest = {
                onAction(CreateJournalEntryAction.OnToggleCancelDialog(false))
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Cancel create journal".uppercase(),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to cancel creating new journal? Your recording will be deleted."
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            onAction(CreateJournalEntryAction.OnToggleCancelDialog(false))
                        }
                    ) {
                        Text(text = "No")
                    }
                    TextButton(
                        onClick = {
                            onAction(CreateJournalEntryAction.OnCancelCreateJournalEntry)
                            onAction(CreateJournalEntryAction.OnToggleCancelDialog(false))
                        }
                    ) {
                        Text(text = "Yes")
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .consumeWindowInsets(WindowInsets.ime)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "New Entry",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 18.dp)
            )
            IconButton(
                onClick = {
                    onAction(CreateJournalEntryAction.OnToggleCancelDialog(true))
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Go back",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable {
                        onAction(CreateJournalEntryAction.OnToggleSelectMoodBottomSheet(true))
                    },
                contentAlignment = Alignment.Center
            ) {
                if (state.selectedMood == null) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                } else {
                    Image(
                        painter = painterResource(getResIdByMood(state.selectedMood)),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = state.title,
                onValueChange = {
                    onAction(CreateJournalEntryAction.OnTitleChange(it))
                },
                textStyle = MaterialTheme.typography.headlineLarge,
                decorationBox = { innerBox ->
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        if (state.title.isBlank()) {
                            Text(
                                text = "Add Title...",
                                style = MaterialTheme.typography.headlineLarge,
                                color = color
                            )
                        }
                        innerBox()
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        AudioPlayer(
            isPlaying = state.isPlaying,
            curPlaybackInSeconds = state.curPlaybackInSeconds,
            maxPlaybackInSeconds = state.maxPlaybackInSeconds,
            moodColors = getMoodColors(state.selectedMood ?: Mood.NEUTRAL),
            onToggle = {
                onAction(CreateJournalEntryAction.OnToggleAudioPlayer)
            },
            onValueChange = {
                onAction(CreateJournalEntryAction.OnSeekCurrentPlayback(it))
            },
            enableSlider = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .onGloballyPositioned {
                            topicsHeight = it.size.height
                        },
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "#",
                        color = color,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    state.selectedTopics.forEach {
                        Topic(
                            text = it,
                            onClick = {},
                            isDeletable = true,
                            onDelete = {
                                onAction(CreateJournalEntryAction.OnDeleteTopic(it))
                            }
                        )
                    }
                    TopicTextField(
                        value = state.inputTopic,
                        onValueChange = {
                            onAction(CreateJournalEntryAction.OnInputTopic(it))
                        },
                        hintColor = color,
                        modifier = Modifier.weight(1f),
                        onFocusChange = {
                            isTopicsFieldFocused = it
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                DescriptionTextField(
                    description = state.description,
                    onValueChange = {
                        onAction(CreateJournalEntryAction.OnDescriptionChange(it))
                    },
                    tint = color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
            }
            if (state.inputTopic.isNotBlank()) {
                TopicFilterDropdown(
                    query = state.inputTopic,
                    topics = state.allTopics,
                    onTopicClick = {
                        onAction(CreateJournalEntryAction.OnSelectTopic(it))
                    },
                    onCreateNewTopicClick = {
                        onAction(CreateJournalEntryAction.OnAddNewTopic)
                    },
                    isFocused = isTopicsFieldFocused,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset {
                            IntOffset(0 , topicsHeight + 8)
                        }
                        .padding(horizontal = 16.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(10.dp))
                )
            }
        }
        CancelAndSaveButton(
            canSave = state.canSave,
            onCancelClick = {
                onAction(CreateJournalEntryAction.OnToggleCancelDialog(true))
            },
            onSaveClick = {
                onAction(CreateJournalEntryAction.OnSaveClick)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        )
    }
}

@Composable
private fun TopicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hintColor: Color,
    modifier: Modifier = Modifier,
    onFocusChange: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerBox ->
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Topic",
                            style = MaterialTheme.typography.bodyMedium,
                            color = hintColor
                        )
                    }
                    innerBox()
                }
            },
            modifier = Modifier.onFocusChanged {
                onFocusChange(it.hasFocus)
            }
        )
    }
}

@Composable
private fun TopicFilterDropdown(
    query: String,
    topics: Set<String>,
    onTopicClick: (String) -> Unit,
    onCreateNewTopicClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    itemVerticalPadding: Dp = 10.dp,
    isFocused: Boolean = false
) {
    val density = LocalDensity.current
    var itemHeight by remember {
        mutableIntStateOf(0)
    }
    var addItemHeight by remember {
        mutableIntStateOf(0)
    }
    val filteredTopics = remember(query, topics, isFocused) {
        if (query.isNotBlank()) {
            topics.filter {
                it.contains(query, true)
            }
        } else {
            if (isFocused) {
                topics.take(3)
            } else emptyList()
        }
    }
    val isNewTopic = query.isNotBlank() && filteredTopics.none { it.equals(query, true) }

    LaunchedEffect(filteredTopics, isNewTopic) {
        if (filteredTopics.isEmpty()) {
            itemHeight = 0
        }
        if (!isNewTopic) {
            addItemHeight = 0
        }
    }

    Box(
        modifier = modifier
            .heightIn(
                max = with(density) {
                    val doubledVerticalPadding = itemVerticalPadding * 2

                    ((itemHeight.toDp() + doubledVerticalPadding) * 3) + (addItemHeight.toDp() + doubledVerticalPadding)
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            filteredTopics.forEach { topic ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onTopicClick(topic)
                        }
                        .padding(horizontal = 12.dp, vertical = itemVerticalPadding)
                        .onGloballyPositioned {
                            if (itemHeight == 0) {
                                itemHeight = it.size.height
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = topic,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            if (isNewTopic) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCreateNewTopicClick()
                        }
                        .padding(horizontal = 12.dp, vertical = itemVerticalPadding)
                        .onGloballyPositioned {
                            if (addItemHeight == 0) {
                                addItemHeight = it.size.height
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Create '$query'",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionTextField(
    description: String,
    onValueChange: (String) -> Unit,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.width(6.dp))
        BasicTextField(
            value = description,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            decorationBox = { innerBox ->
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (description.isBlank()) {
                        Text(
                            text = "Add Description...",
                            color = tint,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    innerBox()
                }
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun CancelAndSaveButton(
    canSave: Boolean,
    onCancelClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onCancelClick,
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
            onClick = onSaveClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = Color.White
            ),
            modifier = Modifier.weight(1f),
            enabled = canSave
        ) {
            Text(
                text = "Save"
            )
        }
    }
}

@Preview
@Composable
private fun CreateJournalEntryScreenPreview() {
    EchoJournalTheme {
        var otherTopics by remember {
           mutableStateOf<Set<String>>(setOf("Jack", "Jared", "Jasper", "January", "Janitor", "Jane", "Jaa", "Jab", "Jaden", "Jagger", "Jah", "Jai", "Jaju", "Jakal", "Jalam", "Jamal"))
        }
        var state by remember {
            mutableStateOf(
                CreateJournalEntryState(
                    allTopics = otherTopics,
                    selectedTopics = setOf("Work", "Conundrum")
                )
            )
        }

        fun filterSelectedTopics() {
            val unselectedTopics = otherTopics.filter { !state.selectedTopics.contains(it) }
            state = state.copy(
                allTopics = unselectedTopics.toSet()
            )
        }

        CreateJournalEntryScreen(
            state = state,
            onAction = { action ->
                when (action) {
                    is CreateJournalEntryAction.OnInputTopic -> {
                        state = state.copy(inputTopic = action.value)
                    }
                    is CreateJournalEntryAction.OnDeleteTopic -> {
                        state = state.copy(
                            selectedTopics = state.selectedTopics - action.value
                        )
                        filterSelectedTopics()
                    }
                    is CreateJournalEntryAction.OnSelectTopic -> {
                        state = state.copy(
                            selectedTopics = state.selectedTopics + action.value,
                            inputTopic = ""
                        )
                        filterSelectedTopics()
                    }
                    CreateJournalEntryAction.OnAddNewTopic -> {
                        val newTopic = state.inputTopic.trim().replaceFirstChar { it.uppercase() }
                        otherTopics = otherTopics + newTopic
                        state = state.copy(
                            selectedTopics = state.selectedTopics + newTopic,
                            allTopics = otherTopics,
                            inputTopic = ""
                        )
                        filterSelectedTopics()
                    }

                    is CreateJournalEntryAction.OnToggleSelectMoodBottomSheet -> {
                        state = state.copy(isSelectMoodBottomSheetOpened = action.isOpen)
                    }
                    is CreateJournalEntryAction.OnSelectMood -> {
                        state = state.copy(selectedMood = action.mood)
                    }
                    is CreateJournalEntryAction.OnTitleChange -> {
                        state = state.copy(title = action.value)
                    }
                    is CreateJournalEntryAction.OnDescriptionChange -> {
                        state = state.copy(description = action.value)
                    }
                    CreateJournalEntryAction.OnToggleAudioPlayer -> {}
                    is CreateJournalEntryAction.OnToggleCancelDialog -> {
                        state = state.copy(isCancelCreateJournalEntryDialogOpened = action.isOpen)
                    }
                    CreateJournalEntryAction.OnCancelCreateJournalEntry -> {}
                    CreateJournalEntryAction.OnSaveClick -> {}
                    is CreateJournalEntryAction.OnSeekCurrentPlayback -> {}
                }
            }
        )
    }
}