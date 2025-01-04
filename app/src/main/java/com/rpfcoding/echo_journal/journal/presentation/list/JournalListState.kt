package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.journal.domain.Journal

data class JournalListState(
    val journals: List<Journal> = emptyList()
)
