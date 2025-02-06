package com.rpfcoding.echo_journal.core.domain.util

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.getInt(unit: DurationUnit): Int {
    return this.toLong(unit).toInt()
}

fun Duration.getHours(): Int {
    return this.getInt(DurationUnit.HOURS)
}

fun Duration.getRemainingMinutes(): Int {
    return this.getInt(DurationUnit.MINUTES) % 60
}

fun Duration.getRemainingSeconds(): Int {
    return this.getInt(DurationUnit.SECONDS) % 60
}