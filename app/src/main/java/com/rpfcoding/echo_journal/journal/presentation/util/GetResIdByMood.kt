package com.rpfcoding.echo_journal.journal.presentation.util

import com.rpfcoding.echo_journal.R
import com.rpfcoding.echo_journal.journal.domain.Mood

fun getResIdByMood(mood: Mood): Int {
    return when (mood) {
        Mood.EXCITED -> R.drawable.ic_excited
        Mood.PEACEFUL -> R.drawable.ic_peaceful
        Mood.NEUTRAL -> R.drawable.ic_neutral
        Mood.SAD -> R.drawable.ic_sad
        Mood.STRESSED -> R.drawable.ic_stressed
    }
}