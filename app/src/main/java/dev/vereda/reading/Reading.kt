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
