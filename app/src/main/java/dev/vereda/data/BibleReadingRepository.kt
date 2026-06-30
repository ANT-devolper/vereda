package dev.vereda.data

import dev.vereda.progress.BibleCatalog
import dev.vereda.reading.ReadingChapter
import dev.vereda.reading.ReadingVerse

/** Loads chapters (book name + verses) for the reading screen. */
interface BibleReadingRepository {
    suspend fun chapter(
        bookId: Int,
        chapter: Int,
    ): ReadingChapter
}

/** Reads verse text from the bundled Bible database and the book name from the [catalog]. */
class DefaultBibleReadingRepository(
    private val verseDao: VerseDao,
    private val catalog: BibleCatalog,
) : BibleReadingRepository {
    override suspend fun chapter(
        bookId: Int,
        chapter: Int,
    ): ReadingChapter {
        val bookName = catalog.books().first { it.id == bookId }.name
        val verses =
            verseDao.getChapter(bookId, chapter).map { ReadingVerse(number = it.verse, text = it.text) }
        return ReadingChapter(bookId = bookId, bookName = bookName, chapter = chapter, verses = verses)
    }
}
