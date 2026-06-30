package dev.vereda.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import java.time.LocalDate

@Dao
interface DailyActivityDao {
    @Upsert
    suspend fun upsert(activity: DailyActivity)

    @Query("SELECT * FROM daily_activity WHERE date = :date")
    suspend fun getByDate(date: LocalDate): DailyActivity?

    /** All dates with activity, ascending — feeds the streak calculation. */
    @Query("SELECT date FROM daily_activity ORDER BY date")
    suspend fun getActivityDates(): List<LocalDate>
}
