package dev.vereda.progress

/**
 * Computes reading progress from the catalog (chapters per book) and the count of distinct chapters read
 * per book. Read counts are clamped to each book's chapter count, so fractions never exceed 1.
 */
class ProgressCalculator {
    fun bookProgress(
        catalog: List<BibleBook>,
        readCountsByBook: Map<Int, Int>,
    ): List<BookProgress> =
        catalog.map { book ->
            val read = (readCountsByBook[book.id] ?: 0).coerceIn(0, book.chapterCount)
            BookProgress(book = book, chaptersRead = read)
        }

    fun overallProgress(
        catalog: List<BibleBook>,
        readCountsByBook: Map<Int, Int>,
    ): OverallProgress {
        val progress = bookProgress(catalog, readCountsByBook)
        return OverallProgress(
            chaptersRead = progress.sumOf { it.chaptersRead },
            totalChapters = catalog.sumOf { it.chapterCount },
        )
    }
}
