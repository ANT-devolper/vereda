package dev.vereda.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * One row per local day on which the user completed at least one chapter.
 * The set of [date]s is the source for the reading streak.
 */
@Entity(tableName = "daily_activity")
data class DailyActivity(
    @PrimaryKey val date: LocalDate,
    val chaptersCompleted: Int,
)
