package com.rpfcoding.echo_journal

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rpfcoding.echo_journal.journal.presentation.create.CreateJournalEntryScreenRoot
import com.rpfcoding.echo_journal.journal.presentation.list.JournalListScreenRoot
import com.rpfcoding.echo_journal.journal.presentation.navigation.JournalGraph
import com.rpfcoding.echo_journal.journal.presentation.settings.SettingsScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = JournalGraph.Root) {
        journalGraph(navController)
    }
}

private fun NavGraphBuilder.journalGraph(navController: NavHostController) {
    navigation<JournalGraph.Root>(startDestination = JournalGraph.JournalListScreen) {
        composable<JournalGraph.JournalListScreen> {
            JournalListScreenRoot(
                onNavigateToCreateJournal = { id, fileUri ->
                    navController.navigate(JournalGraph.CreateJournalScreen(id, fileUri))
                },
                onNavigateToSettings = {
                    navController.navigate(JournalGraph.JournalSettings)
                }
            )
        }
        composable<JournalGraph.CreateJournalScreen> {
            CreateJournalEntryScreenRoot(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
        composable<JournalGraph.JournalSettings> {
            SettingsScreenRoot(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}