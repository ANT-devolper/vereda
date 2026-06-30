package dev.vereda.data

import androidx.room.TypeConverter
import java.time.Instant

/** Stores [Instant] as epoch milliseconds. */
class InstantConverters {
    @TypeConverter
    fun fromEpochMilli(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun toEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()
}
