package com.rpfcoding.echo_journal

import android.app.Application
import com.rpfcoding.echo_journal.core.audio.data.di.coreAudioDataModule
import com.rpfcoding.echo_journal.core.database.di.coreDatabaseModule
import com.rpfcoding.echo_journal.core.file.data.di.coreFileDataModule
import com.rpfcoding.echo_journal.journal.data.di.journalDataModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class EchoJournalApp: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@EchoJournalApp)
            modules(
                coreAudioDataModule,
                coreDatabaseModule,
                coreFileDataModule,
                journalDataModule,
                appModule
            )
        }
    }
}