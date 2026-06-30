package dev.vereda.progress

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reading progress is derived from the catalog (chapters per book) and the count of distinct chapters
 * read per book. Pure logic, independent of persistence.
 */
class ProgressCalculatorTest {
    private val calculator = ProgressCalculator()
    private val genesis = BibleBook(id = 1, name = "Genesis", chapterCount = 50)
    private val exodus = BibleBook(id = 2, name = "Exodus", chapterCount = 40)
    private val catalog = listOf(genesis, exodus)

    @Test
    fun `no reads yields zero progress for every book and overall`() {
        val books = calculator.bookProgress(catalog, readCountsByBook = emptyMap())

        assertEquals(listOf(genesis, exodus), books.map { it.book })
        assertEquals(0, books[0].chaptersRead)
        assertEquals(0f, books[0].fraction, 0.0001f)
        assertFalse(books[0].isComplete)

        val overall = calculator.overallProgress(catalog, readCountsByBook = emptyMap())
        assertEquals(0, overall.chaptersRead)
        assertEquals(90, overall.totalChapters)
        assertEquals(0f, overall.fraction, 0.0001f)
    }

    @Test
    fun `partial reads produce the right fractions`() {
        val reads = mapOf(genesis.id to 10, exodus.id to 20)

        val books = calculator.bookProgress(catalog, reads)
        assertEquals(0.2f, books[0].fraction, 0.0001f)
        assertEquals(0.5f, books[1].fraction, 0.0001f)

        val overall = calculator.overallProgress(catalog, reads)
        assertEquals(30, overall.chaptersRead)
        assertEquals(90, overall.totalChapters)
        assertEquals(30f / 90f, overall.fraction, 0.0001f)
    }

    @Test
    fun `a fully read book is complete`() {
        val books = calculator.bookProgress(catalog, mapOf(genesis.id to 50))

        assertTrue(books[0].isComplete)
        assertEquals(1f, books[0].fraction, 0.0001f)
    }

    @Test
    fun `read counts are clamped to the chapter count`() {
        val books = calculator.bookProgress(catalog, mapOf(genesis.id to 60))

        assertEquals(50, books[0].chaptersRead)
        assertEquals(1f, books[0].fraction, 0.0001f)
        assertTrue(books[0].isComplete)

        val overall = calculator.overallProgress(catalog, mapOf(genesis.id to 60))
        assertEquals(50, overall.chaptersRead)
    }
}
