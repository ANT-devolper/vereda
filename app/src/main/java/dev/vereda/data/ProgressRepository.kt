package dev.vereda.data

import dev.vereda.progress.BibleCatalog
import dev.vereda.progress.BookProgress
import dev.vereda.progress.OverallProgress
import dev.vereda.progress.ProgressCalculator
import java.time.Clock
import java.time.Instant

/**
 * Records read chapters and reports reading progress per book and overall.
 *
 * The [clock] is injectable so the first-read timestamp is deterministic in tests.
 */
class ProgressRepository(
    private val dao: ChapterReadDao,
    private val catalog: BibleCatalog,
    private val calculator: ProgressCalculator = ProgressCalculator(),
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    /** Marks a chapter as read. Re-marking is a no-op (the first read is kept). */
    suspend fun markChapterRead(
        bookId: Int,
        chapter: Int,
    ) {
        dao.markRead(ChapterRead(bookId = bookId, chapter = chapter, firstReadAt = Instant.now(clock)))
    }

    suspend fun bookProgress(): List<BookProgress> = calculator.bookProgress(catalog.books(), readCountsByBook())

    suspend fun overallProgress(): OverallProgress = calculator.overallProgress(catalog.books(), readCountsByBook())

    private suspend fun readCountsByBook(): Map<Int, Int> = dao.readCountsByBook().associate { it.bookId to it.chaptersRead }
}
