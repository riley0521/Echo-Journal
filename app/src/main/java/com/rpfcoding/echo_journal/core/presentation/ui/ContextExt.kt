package com.rpfcoding.echo_journal.core.presentation.ui

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.showToastStr(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToastRes(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}