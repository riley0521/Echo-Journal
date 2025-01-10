package com.rpfcoding.echo_journal.core.presentation.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    primaryContainer = Blue100,
    onPrimaryContainer = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    errorContainer = ErrorContainer,
    error = Error,
    background = SecondaryContainer,
    surfaceVariant = SurfaceVariant,
    secondary = Secondary,
    surfaceContainer = SurfaceContainer
)

@Composable
fun EchoJournalTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}