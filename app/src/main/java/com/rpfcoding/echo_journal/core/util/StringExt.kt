package com.rpfcoding.echo_journal.core.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun formatLocalDateTimeToHourMinute(value: LocalDateTime): String {
    return String.format(Locale.getDefault(), "%02d:%02d", value.hour, value.minute)
}

fun getDisplayTextByDate(value: LocalDate): String {
    val now = LocalDate.now()
    val yesterday = now.dayOfYear - 1
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
    val formatterWithYear = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")

    return when {
        now.dayOfYear == value.dayOfYear -> "Today"
        yesterday == value.dayOfYear -> "Yesterday"
        else -> {
            if (now.year != value.year) {
                formatterWithYear.format(value)
            } else {
                formatter.format(value)
            }
        }
    }
}

fun formatSecondsToMinSecond(value: Long): String {
    val totalTimeDuration = value.toDuration(DurationUnit.SECONDS)
    val minutes = totalTimeDuration.getMinutes()
    val seconds = totalTimeDuration.getRemainingSeconds()

    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}