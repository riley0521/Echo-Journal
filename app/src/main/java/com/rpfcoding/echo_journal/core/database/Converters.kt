package com.rpfcoding.echo_journal.core.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun setToString(value: Set<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun stringToSet(value: String): Set<String> {
        return Json.decodeFromString<Set<String>>(value)
    }
}