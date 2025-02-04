package com.rpfcoding.echo_journal.journal.presentation.settings

import com.rpfcoding.echo_journal.journal.domain.Mood

sealed interface SettingsAction {
    data class OnSelectMood(val mood: Mood): SettingsAction
    data class OnInputTopic(val value: String): SettingsAction
    data class OnTopicFieldFocusChange(val isFocused: Boolean): SettingsAction
    data class OnSelectTopic(val topic: String): SettingsAction
    data object OnAddNewTopic: SettingsAction
    data class OnDeleteTopic(val topic: String): SettingsAction
    data object OnNavigateBack: SettingsAction
}