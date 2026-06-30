package dev.vereda.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/** The app's local Room database. */
@Database(
    entities = [DailyActivity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(LocalDateConverters::class)
abstract class VeredaDatabase : RoomDatabase() {
    abstract fun dailyActivityDao(): DailyActivityDao
}
