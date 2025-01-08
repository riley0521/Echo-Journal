package com.rpfcoding.echo_journal.core.util

import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.getInt(unit: DurationUnit): Int {
    return this.toLong(unit).toInt()
}

fun Duration.getMinutes(): Int {
    return this.getInt(DurationUnit.MINUTES)
}

fun Duration.getRemainingSeconds(): Int {
    return this.getInt(DurationUnit.SECONDS) % 60
}