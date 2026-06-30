package dev.vereda.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface VerseDao {
    /** All verses of a chapter, in order. */
    @Query("SELECT * FROM verse WHERE bookId = :bookId AND chapter = :chapter ORDER BY verse")
    suspend fun getChapter(
        bookId: Int,
        chapter: Int,
    ): List<Verse>
}
