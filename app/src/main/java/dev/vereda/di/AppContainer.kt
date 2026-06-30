package dev.vereda.di

import android.content.Context
import androidx.room.Room
import dev.vereda.data.BibleContentDatabase
import dev.vereda.data.BibleReadingRepository
import dev.vereda.data.DefaultBibleReadingRepository
import dev.vereda.data.DefaultProgressRepository
import dev.vereda.data.DefaultStreakRepository
import dev.vereda.data.ProgressRepository
import dev.vereda.data.StreakRepository
import dev.vereda.data.VeredaDatabase
import dev.vereda.progress.BibleCatalog
import dev.vereda.progress.PortugueseBibleCatalog

/** Application-wide dependencies, resolved manually (no DI framework for now). */
interface AppContainer {
    val streakRepository: StreakRepository
    val progressRepository: ProgressRepository
    val bibleReadingRepository: BibleReadingRepository
    val bibleCatalog: BibleCatalog
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

    private val catalog = PortugueseBibleCatalog()

    override val bibleCatalog: BibleCatalog get() = catalog

    override val streakRepository: StreakRepository by lazy {
        DefaultStreakRepository(dao = database.dailyActivityDao())
    }

    override val progressRepository: ProgressRepository by lazy {
        DefaultProgressRepository(dao = database.chapterReadDao(), catalog = catalog)
    }

    override val bibleReadingRepository: BibleReadingRepository by lazy {
        DefaultBibleReadingRepository(verseDao = bibleDatabase.verseDao(), catalog = catalog)
    }
}
