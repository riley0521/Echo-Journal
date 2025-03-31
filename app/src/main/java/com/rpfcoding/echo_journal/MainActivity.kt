package com.rpfcoding.echo_journal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.rpfcoding.echo_journal.core.presentation.designsystem.EchoJournalTheme

class MainActivity : ComponentActivity() {

    private var shouldCreateJournal by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        parseIntent(intent)

        setContent {
            EchoJournalTheme {
                val navController = rememberNavController()
                NavigationRoot(
                    navController,
                    shouldCreateJournal = shouldCreateJournal,
                    onJournalCreating = {
                        shouldCreateJournal = false
                    }
                )
            }
        }
    }

    private fun parseIntent(intent: Intent) {
        if (intent.action == "com.rpfcoding.echo_journal.CREATE_JOURNAL") {
            shouldCreateJournal = true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }
}