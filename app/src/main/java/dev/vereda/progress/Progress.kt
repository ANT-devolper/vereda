package dev.vereda.progress

/** A book in the Bible catalog, with its total number of chapters. */
data class BibleBook(
    val id: Int,
    val name: String,
    val chapterCount: Int,
)

/** Reading progress for a single book. */
data class BookProgress(
    val book: BibleBook,
    val chaptersRead: Int,
) {
    val fraction: Float
        get() = if (book.chapterCount == 0) 0f else chaptersRead.toFloat() / book.chapterCount

    val isComplete: Boolean
        get() = book.chapterCount > 0 && chaptersRead >= book.chapterCount
}

/** Reading progress across the whole Bible. */
data class OverallProgress(
    val chaptersRead: Int,
    val totalChapters: Int,
) {
    val fraction: Float
        get() = if (totalChapters == 0) 0f else chaptersRead.toFloat() / totalChapters
}
