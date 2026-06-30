package dev.vereda.data

import androidx.room.TypeConverter
import java.time.LocalDate

/** Stores [LocalDate] as its epoch day (a Long), which keeps rows sortable by date. */
class LocalDateConverters {
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)

    @TypeConverter
    fun toEpochDay(date: LocalDate?): Long? = date?.toEpochDay()
}
