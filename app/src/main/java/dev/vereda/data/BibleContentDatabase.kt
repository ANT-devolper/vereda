package dev.vereda.data

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Read-only database holding the bundled Bible text ("Bíblia Livre", CC BY 4.0).
 * It is pre-populated from `assets/bible.db` via Room.createFromAsset and never written to.
 */
@Database(
    entities = [Verse::class],
    version = 1,
    exportSchema = true,
)
abstract class BibleContentDatabase : RoomDatabase() {
    abstract fun verseDao(): VerseDao
}
