package dev.vereda.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.time.Instant

@Dao
interface ChapterReadDao {
    /** Records a chapter as read. Ignores conflicts so the first read timestamp is preserved. */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markRead(chapter: ChapterRead)

    @Query("SELECT COUNT(*) FROM chapter_read")
    suspend fun totalChaptersRead(): Int

    @Query("SELECT firstReadAt FROM chapter_read WHERE bookId = :bookId AND chapter = :chapter")
    suspend fun getFirstReadAt(
        bookId: Int,
        chapter: Int,
    ): Instant?

    /** Distinct chapters read per book — feeds per-book progress. */
    @Query("SELECT bookId, COUNT(*) AS chaptersRead FROM chapter_read GROUP BY bookId")
    suspend fun readCountsByBook(): List<BookReadCount>
}
