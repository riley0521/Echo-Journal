package com.rpfcoding.echo_journal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.rpfcoding.echo_journal.journal.presentation.create.CreateJournalEntryScreenRoot
import com.rpfcoding.echo_journal.journal.presentation.list.JournalListAction
import com.rpfcoding.echo_journal.journal.presentation.list.JournalListScreenRoot
import com.rpfcoding.echo_journal.journal.presentation.list.JournalListViewModel
import com.rpfcoding.echo_journal.journal.presentation.navigation.JournalGraph
import com.rpfcoding.echo_journal.journal.presentation.settings.SettingsScreenRoot
import com.rpfcoding.echo_journal.journal.presentation.util.hasRecordAudioPermission
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationRoot(
    navController: NavHostController,
    shouldCreateJournal: Boolean = false,
    onJournalCreating: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = JournalGraph.Root) {
        journalGraph(
            navController = navController,
            shouldCreateJournal = shouldCreateJournal,
            onJournalCreating = onJournalCreating
        )
    }
}

private fun NavGraphBuilder.journalGraph(
    navController: NavHostController,
    shouldCreateJournal: Boolean = false,
    onJournalCreating: () -> Unit = {}
) {
    navigation<JournalGraph.Root>(startDestination = JournalGraph.JournalListScreen) {
        composable<JournalGraph.JournalListScreen> {
            val context = LocalContext.current
            val viewModel = koinViewModel<JournalListViewModel>()

            LaunchedEffect(shouldCreateJournal) {
                if (shouldCreateJournal && context.hasRecordAudioPermission()) {
                    viewModel.onAction(JournalListAction.OnRecordPermissionGranted(true))
                    viewModel.onAction(JournalListAction.OnToggleRecordingBottomSheet(true))
                    viewModel.onAction(JournalListAction.OnToggleRecord)

                    onJournalCreating()
                }
            }

            JournalListScreenRoot(
                onNavigateToCreateJournal = { id, fileUri ->
                    navController.navigate(JournalGraph.CreateJournalScreen(id, fileUri))
                },
                onNavigateToSettings = {
                    navController.navigate(JournalGraph.JournalSettings)
                },
                viewModel = viewModel
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