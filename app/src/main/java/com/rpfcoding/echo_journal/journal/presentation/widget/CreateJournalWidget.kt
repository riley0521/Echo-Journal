package com.rpfcoding.echo_journal.journal.presentation.widget

import android.content.Context
import android.content.Intent
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import com.rpfcoding.echo_journal.MainActivity

object CreateJournalWidget: GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Button(
                text = "How are you doing?",
                onClick = actionStartActivity(
                    intent = Intent(context, MainActivity::class.java).apply {
                        action = "com.rpfcoding.echo_journal.CREATE_JOURNAL"
                    }
                )
            )
        }
    }
}

class CreateJournalWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = CreateJournalWidget
}