package dev.vereda.data

import androidx.room.Entity
import java.time.Instant

/**
 * One row per distinct chapter the user has read, keyed by [bookId] + [chapter].
 * [firstReadAt] records when it was first read and is preserved on re-marking.
 * The set of rows is the source for per-book and overall reading progress.
 */
@Entity(tableName = "chapter_read", primaryKeys = ["bookId", "chapter"])
data class ChapterRead(
    val bookId: Int,
    val chapter: Int,
    val firstReadAt: Instant,
)

/** Number of distinct chapters read in a book, as returned by a grouped query. */
data class BookReadCount(
    val bookId: Int,
    val chaptersRead: Int,
)
