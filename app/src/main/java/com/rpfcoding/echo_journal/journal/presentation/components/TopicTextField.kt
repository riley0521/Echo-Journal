package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color

@Composable
fun TopicTextField(
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