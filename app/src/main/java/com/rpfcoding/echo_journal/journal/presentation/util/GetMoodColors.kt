package com.rpfcoding.echo_journal.journal.presentation.util

import com.rpfcoding.echo_journal.core.presentation.designsystem.ExcitedContainer
import com.rpfcoding.echo_journal.core.presentation.designsystem.ExcitedPrimary
import com.rpfcoding.echo_journal.core.presentation.designsystem.ExcitedSecondary
import com.rpfcoding.echo_journal.core.presentation.designsystem.NeutralContainer
import com.rpfcoding.echo_journal.core.presentation.designsystem.NeutralPrimary
import com.rpfcoding.echo_journal.core.presentation.designsystem.NeutralSecondary
import com.rpfcoding.echo_journal.core.presentation.designsystem.PeacefulContainer
import com.rpfcoding.echo_journal.core.presentation.designsystem.PeacefulPrimary
import com.rpfcoding.echo_journal.core.presentation.designsystem.PeacefulSecondary
import com.rpfcoding.echo_journal.core.presentation.designsystem.SadContainer
import com.rpfcoding.echo_journal.core.presentation.designsystem.SadPrimary
import com.rpfcoding.echo_journal.core.presentation.designsystem.SadSecondary
import com.rpfcoding.echo_journal.core.presentation.designsystem.StressedContainer
import com.rpfcoding.echo_journal.core.presentation.designsystem.StressedPrimary
import com.rpfcoding.echo_journal.core.presentation.designsystem.StressedSecondary
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.MoodColors

fun getMoodColors(mood: Mood): MoodColors {
    return when (mood) {
        Mood.EXCITED -> MoodColors(
            primary = ExcitedPrimary,
            secondary = ExcitedSecondary,
            container = ExcitedContainer
        )
        Mood.PEACEFUL -> MoodColors(
            primary = PeacefulPrimary,
            secondary = PeacefulSecondary,
            container = PeacefulContainer
        )
        Mood.NEUTRAL -> MoodColors(
            primary = NeutralPrimary,
            secondary = NeutralSecondary,
            container = NeutralContainer
        )
        Mood.SAD -> MoodColors(
            primary = SadPrimary,
            secondary = SadSecondary,
            container = SadContainer
        )
        Mood.STRESSED -> MoodColors(
            primary = StressedPrimary,
            secondary = StressedSecondary,
            container = StressedContainer
        )
    }
}