package com.rpfcoding.echo_journal.journal.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme

@Composable
fun BottomSheetWithHeaderAndFooter(
    hideFooter: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(color = MaterialTheme.colorScheme.surface)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp, 4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(color = Color(0xffc1c3ce))
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        content()
        Spacer(modifier = Modifier.height(10.dp))
        if (!hideFooter) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp, 4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(color = Color.Black)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Preview
@Composable
private fun BottomSheetWithHeaderPreview() {
    EchoJournalTheme {
        BottomSheetWithHeaderAndFooter(
            content = {}
        )
    }
}