package com.rpfcoding.echo_journal.journal.presentation.navigation

import kotlinx.serialization.Serializable

object JournalGraph {

    @Serializable
    data object Root

    @Serializable
    data object JournalListScreen

    @Serializable
    data class CreateJournalScreen(val id: String, val fileUri: String)
}