package com.rpfcoding.echo_journal.journal.presentation.list

import com.rpfcoding.echo_journal.journal.domain.Journal
import com.rpfcoding.echo_journal.journal.presentation.components.JournalFilterType
import com.rpfcoding.echo_journal.journal.presentation.components.TopicUi
import com.rpfcoding.echo_journal.journal.presentation.components.getMoodsFilterType

data class JournalListState(
    val journals: List<Journal> = emptyList(),
    val filteredTopics: JournalFilterType.Topics = JournalFilterType.Topics(dummyTopics),
    val filteredMoods: JournalFilterType.Moods = getMoodsFilterType()
)

val dummyTopics = setOf(
    TopicUi("Work", false),
    TopicUi("Conundrums", false),
    TopicUi("Gym", false),
    TopicUi("Love", false)
)
