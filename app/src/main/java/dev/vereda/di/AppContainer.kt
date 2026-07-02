package dev.vereda.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import dev.vereda.appicon.AlarmAppIconScheduler
import dev.vereda.appicon.AppIconUpdater
import dev.vereda.appicon.PackageManagerAppIconApplier
import dev.vereda.data.BibleContentDatabase
import dev.vereda.data.BibleReadingRepository
import dev.vereda.data.DefaultBibleReadingRepository
import dev.vereda.data.DefaultProgressRepository
import dev.vereda.data.DefaultReadingHistoryRepository
import dev.vereda.data.DefaultStreakRepository
import dev.vereda.data.ProgressRepository
import dev.vereda.data.ReadingHistoryRepository
import dev.vereda.data.StreakRepository
import dev.vereda.data.VeredaDatabase
import dev.vereda.progress.BibleCatalog
import dev.vereda.progress.PortugueseBibleCatalog
import dev.vereda.reminders.AlarmReminderScheduler
import dev.vereda.reminders.ReminderScheduler
import dev.vereda.settings.DefaultOnboardingRepository
import dev.vereda.settings.DefaultReminderRepository
import dev.vereda.settings.OnboardingRepository
import dev.vereda.settings.ReminderRepository

/** Application-wide dependencies, resolved manually (no DI framework for now). */
interface AppContainer {
    val streakRepository: StreakRepository
    val progressRepository: ProgressRepository
    val readingHistoryRepository: ReadingHistoryRepository
    val bibleReadingRepository: BibleReadingRepository
    val bibleCatalog: BibleCatalog
    val reminderRepository: ReminderRepository
    val onboardingRepository: OnboardingRepository
    val reminderScheduler: ReminderScheduler
    val appIconUpdater: AppIconUpdater
}

/** Builds the Room-backed repositories used in production. */
class DefaultAppContainer(
    context: Context,
) : AppContainer {
    private val database: VeredaDatabase by lazy {
        Room.databaseBuilder(context, VeredaDatabase::class.java, "vereda.db").build()
    }

    private val bibleDatabase: BibleContentDatabase by lazy {
        Room
            .databaseBuilder(context, BibleContentDatabase::class.java, "bible-content.db")
            .createFromAsset("bible.db")
            .build()
    }

    private val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("settings") })
    }

    private val catalog = PortugueseBibleCatalog()

    override val bibleCatalog: BibleCatalog get() = catalog

    override val reminderRepository: ReminderRepository by lazy { DefaultReminderRepository(dataStore) }

    override val onboardingRepository: OnboardingRepository by lazy { DefaultOnboardingRepository(dataStore) }

    override val reminderScheduler: ReminderScheduler by lazy { AlarmReminderScheduler(context) }

    override val streakRepository: StreakRepository by lazy {
        DefaultStreakRepository(dao = database.dailyActivityDao())
    }

    override val appIconUpdater: AppIconUpdater by lazy {
        AppIconUpdater(
            streakRepository = streakRepository,
            applier = PackageManagerAppIconApplier(context),
            scheduler = AlarmAppIconScheduler(context),
        )
    }

    override val progressRepository: ProgressRepository by lazy {
        DefaultProgressRepository(dao = database.chapterReadDao(), catalog = catalog)
    }

    override val readingHistoryRepository: ReadingHistoryRepository by lazy {
        DefaultReadingHistoryRepository(dao = database.chapterReadDao(), catalog = catalog)
    }

    override val bibleReadingRepository: BibleReadingRepository by lazy {
        DefaultBibleReadingRepository(verseDao = bibleDatabase.verseDao(), catalog = catalog)
    }
}
