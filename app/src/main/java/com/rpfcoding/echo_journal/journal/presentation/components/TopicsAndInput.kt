package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowScope.TopicsAndInput(
    color: Color,
    topics: Set<String>,
    input: String,
    onInputChange: (String) -> Unit,
    onDelete: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    showAddButton: Boolean = false,
    itemHeight: Dp = 32.dp
) {
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember {
        mutableStateOf(false)
    }
    val topicHeightModifier = if (showAddButton) {
        Modifier.height(itemHeight)
    } else {
        Modifier
    }

    topics.forEach {
        Topic(
            text = it,
            onClick = {},
            isDeletable = true,
            onDelete = {
                onDelete(it)
            },
            modifier = topicHeightModifier
        )
    }
    if (!isFocused && showAddButton) {
        Box(
            modifier = Modifier
                .size(itemHeight)
                .clip(RoundedCornerShape(100.dp))
                .background(Color(0xfff2f2f7))
                .clickable {
                    focusRequester.requestFocus()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
        }
    }
    TopicTextField(
        value = input,
        onValueChange = {
            onInputChange(it)
        },
        hintColor = color,
        modifier = Modifier
            .weight(1f),
        onFocusChange = {
            isFocused = it
            onFocusChange(it)
        },
        focusRequester = focusRequester,
        showText = isFocused || !showAddButton
    )
}