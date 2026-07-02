package dev.vereda.reading

/** A single verse as shown on the reading screen. */
data class ReadingVerse(
    val number: Int,
    val text: String,
)

/** A chapter ready to be read: the book name, chapter number and its verses in order. */
data class ReadingChapter(
    val bookId: Int,
    val bookName: String,
    val chapter: Int,
    val verses: List<ReadingVerse>,
)

/** A chapter the user can navigate to next (next chapter of the book, or chapter 1 of the next book). */
data class ChapterTarget(
    val bookId: Int,
    val chapter: Int,
)
