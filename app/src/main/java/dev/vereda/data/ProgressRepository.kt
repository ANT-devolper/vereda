package dev.vereda.data

import dev.vereda.progress.BibleCatalog
import dev.vereda.progress.BookProgress
import dev.vereda.progress.OverallProgress
import dev.vereda.progress.ProgressCalculator
import java.time.Clock
import java.time.Instant

/** Records read chapters and reports reading progress per book and overall. */
interface ProgressRepository {
    /** Marks a chapter as read. Re-marking is a no-op (the first read is kept). */
    suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    )

    /** Whether the given chapter has already been marked as read. */
    suspend fun isChapterRead(
        bookId: Int,
        chapter: Int,
    ): Boolean

    suspend fun bookProgress(): List<BookProgress>

    suspend fun overallProgress(): OverallProgress
}

/**
 * Room-backed [ProgressRepository].
 *
 * The [clock] is injectable so the first-read timestamp is deterministic in tests.
 */
class DefaultProgressRepository(
    private val dao: ChapterReadDao,
    private val catalog: BibleCatalog,
    private val calculator: ProgressCalculator = ProgressCalculator(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : ProgressRepository {
    override suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) {
        dao.markRead(ChapterRead(bookId = bookId, chapter = chapter, firstReadAt = Instant.now(clock)))
    }

    override suspend fun isChapterRead(
        bookId: Int,
        chapter: Int,
    ): Boolean = dao.getFirstReadAt(bookId, chapter) != null

    override suspend fun bookProgress(): List<BookProgress> = calculator.bookProgress(catalog.books(), readCountsByBook())

    override suspend fun overallProgress(): OverallProgress = calculator.overallProgress(catalog.books(), readCountsByBook())

    private suspend fun readCountsByBook(): Map<Int, Int> = dao.readCountsByBook().associate { it.bookId to it.chaptersRead }
}
