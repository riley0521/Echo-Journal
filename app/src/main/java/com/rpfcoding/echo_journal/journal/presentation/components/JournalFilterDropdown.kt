package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.invisibleToUser
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.util.getMoodUiByMood

data class MoodUi(
    @DrawableRes val resId: Int,
    val name: String,
    val isSelected: Boolean
)

data class TopicUi(
    val name: String,
    val isSelected: Boolean
)

sealed interface JournalFilterType {
    data class Moods(val moods: Set<MoodUi>) : JournalFilterType
    data class Topics(val topics: Set<TopicUi>) : JournalFilterType

    fun getSize(): Int {
        return when (this) {
            is Moods -> moods.size
            is Topics -> topics.size
        }
    }
}

fun getMoodsFilterType(): JournalFilterType.Moods {
    return JournalFilterType.Moods(Mood.entries.map { getMoodUiByMood(it) }.toSet())
}

@Composable
fun <T : JournalFilterType> JournalFilterDropdown(
    title: String,
    filterType: T,
    onToggle: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemVerticalPadding: Dp = 8.dp
) {
    val density = LocalDensity.current

    var expanded by remember {
        mutableStateOf(false)
    }

    var itemHeight by remember {
        mutableIntStateOf(0)
    }
    val itemHeightDp by remember {
        derivedStateOf {
            with(density) {
                itemHeight.toDp().plus(itemVerticalPadding)
            }
        }
    }
    val offsetWidth = when (filterType) {
        is JournalFilterType.Moods -> 32.dp
        is JournalFilterType.Topics -> 16.dp
        else -> 0.dp
    }
    val offsetX = when (filterType) {
        is JournalFilterType.Topics -> 16.dp
        else -> 0.dp
    }
    val menuWidth = LocalConfiguration.current.screenWidthDp.dp - offsetWidth

    Column(
        modifier = modifier
    ) {
        val dropdownSelectedBackgroundColor = if (expanded) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            Color(0xffc1c3ce)
        }
        val shape = RoundedCornerShape(999.dp)
        val backgroundModifier = if (expanded) {
            Modifier.background(Color.White, shape = shape)
        } else {
            Modifier.background(Color.Transparent, shape = shape)
        }

        Box(
            modifier = Modifier
                .border(width = 1.dp, color = dropdownSelectedBackgroundColor, shape)
                .then(backgroundModifier)
                .clickable {
                    expanded = true
                }
                .animateContentSize()
        ) {
            val paddingStart = if (filterType is JournalFilterType.Moods && filterType
                    .moods
                    .none { it.isSelected } ||
                filterType is JournalFilterType.Topics
            ) {
                12.dp
            } else {
                0.dp
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .padding(start = paddingStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (filterType is JournalFilterType.Moods) {
                    val selectedMoods = filterType.moods.filter { it.isSelected }.toSet()

                    if (selectedMoods.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    MoodImages(
                        selectedMoods = selectedMoods,
                        modifier = Modifier.size(22.dp)
                    )
                    if (selectedMoods.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                Text(
                    text = getTitle(title, filterType),
                    modifier = Modifier.padding(end = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        DropdownMenu(
            modifier = Modifier
                .width(menuWidth)
                .heightIn(
                    min = itemHeightDp.times(
                        filterType
                            .getSize()
                            .coerceAtMost(3)
                    ),
                    max = itemHeightDp.times(
                        filterType
                            .getSize()
                            .coerceAtMost(5)
                    )
                ),
            expanded = expanded,
            offset = DpOffset(offsetX, 0.dp),
            onDismissRequest = {
                expanded = false
            },
            shape = RoundedCornerShape(10.dp),
            containerColor = Color.White
        ) {
            when (filterType) {
                is JournalFilterType.Moods -> {
                    filterType.moods.forEach { mood ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(mood.resId),
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = mood.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        ),
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (mood.isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            onClick = {
                                val updated = JournalFilterType.Moods(
                                    moods = filterType.moods.map {
                                        if (mood.name == it.name) {
                                            it.copy(isSelected = !it.isSelected)
                                        } else it
                                    }.toSet()
                                )
                                onToggle(updated as T)
                            },
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = itemVerticalPadding
                            ),
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .background(
                                    getSelectedBackgroundColor(mood.isSelected),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .onGloballyPositioned {
                                    itemHeight = it.size.height
                                }
                        )
                    }
                }

                is JournalFilterType.Topics -> {
                    filterType.topics.forEach { topic ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        modifier = Modifier.semantics {
                                            // For accessibility, we don't want the talkback to read this.
                                            this.invisibleToUser()
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = topic.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (topic.isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            tint = MaterialTheme.colorScheme.primary,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            onClick = {
                                val updated = JournalFilterType.Topics(
                                    topics = filterType.topics.map {
                                        if (topic.name == it.name) {
                                            it.copy(isSelected = !it.isSelected)
                                        } else it
                                    }.toSet()
                                )
                                onToggle(updated as T)
                            },
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = itemVerticalPadding
                            ),
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .background(
                                    getSelectedBackgroundColor(topic.isSelected),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .onGloballyPositioned {
                                    itemHeight = it.size.height
                                }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getSelectedBackgroundColor(isSelected: Boolean): Color {
    return if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.05f)
    } else {
        Color.Transparent
    }
}

@Composable
private fun getTitle(title: String, filterType: JournalFilterType): String {
    val selectedFilters = when (filterType) {
        is JournalFilterType.Moods -> {
            filterType.moods.filter { it.isSelected }.map { it.name }
        }

        is JournalFilterType.Topics -> {
            filterType.topics.filter { it.isSelected }.map { it.name }.sorted()
        }
    }
    val altTitle = buildList {
        addAll(selectedFilters.take(2).map { it }.toTypedArray())
        if (selectedFilters.size > 2) {
            add("+${selectedFilters.size - 2}")
        }
    }.joinToString(", ")

    return altTitle.ifBlank { title }
}

@Composable
private fun MoodImages(
    selectedMoods: Set<MoodUi>,
    modifier: Modifier = Modifier
) {
    selectedMoods.forEach { mood ->
        Image(
            painter = painterResource(mood.resId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun JournalFilterDropdownPreview() {
    EchoJournalTheme {
        JournalFilterDropdown(
            title = "All Moods",
            filterType = getMoodsFilterType(),
            onToggle = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}