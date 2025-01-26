package com.rpfcoding.echo_journal.journal.presentation.util

import android.Manifest
import android.content.Context
import androidx.activity.ComponentActivity
import com.rpfcoding.echo_journal.core.presentation.util.hasPermission

fun ComponentActivity.shouldShowRecordAudioPermissionRationale(): Boolean {
    return shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)
}

fun Context.hasRecordAudioPermission(): Boolean {
    return hasPermission(Manifest.permission.RECORD_AUDIO)
}