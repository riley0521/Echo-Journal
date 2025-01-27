package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TopicFilterDropdown(
    query: String,
    filteredTopics: Set<String>,
    isNewTopic: Boolean,
    onTopicClick: (String) -> Unit,
    onCreateNewTopicClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    itemVerticalPadding: Dp = 10.dp
) {
    val density = LocalDensity.current
    var itemHeight by remember {
        mutableIntStateOf(0)
    }
    var addItemHeight by remember {
        mutableIntStateOf(0)
    }

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