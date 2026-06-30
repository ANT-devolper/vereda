package dev.vereda.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** The app's local Room database. */
@Database(
    entities = [DailyActivity::class, ChapterRead::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(LocalDateConverters::class, InstantConverters::class)
abstract class VeredaDatabase : RoomDatabase() {
    abstract fun dailyActivityDao(): DailyActivityDao

    abstract fun chapterReadDao(): ChapterReadDao
}
