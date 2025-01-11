package com.rpfcoding.echo_journal.journal.presentation.util

import com.rpfcoding.echo_journal.R
import com.rpfcoding.echo_journal.journal.domain.Mood
import com.rpfcoding.echo_journal.journal.presentation.components.MoodUi

fun getResIdByMood(mood: Mood): Int {
    return when (mood) {
        Mood.EXCITED -> R.drawable.ic_excited
        Mood.PEACEFUL -> R.drawable.ic_peaceful
        Mood.NEUTRAL -> R.drawable.ic_neutral
        Mood.SAD -> R.drawable.ic_sad
        Mood.STRESSED -> R.drawable.ic_stressed
    }
}

fun getMoodOutlined(mood: Mood): Int {
    return when (mood) {
        Mood.EXCITED -> R.drawable.ic_excited_outline
        Mood.PEACEFUL -> R.drawable.ic_peaceful_outline
        Mood.NEUTRAL -> R.drawable.ic_neutral_outline
        Mood.SAD -> R.drawable.ic_sad_outline
        Mood.STRESSED -> R.drawable.ic_stressed_outline
    }
}

fun getMoodUiByMood(mood: Mood): MoodUi {
    val resId = getResIdByMood(mood)
    val name = when (mood) {
        Mood.EXCITED -> "Excited"
        Mood.PEACEFUL -> "Peaceful"
        Mood.NEUTRAL -> "Neutral"
        Mood.SAD -> "Sad"
        Mood.STRESSED -> "Stressed"
    }

    return MoodUi(
        resId = resId,
        name = name,
        isSelected = false
    )
}