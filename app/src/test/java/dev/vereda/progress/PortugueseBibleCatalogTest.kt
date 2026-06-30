package dev.vereda.progress

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * The Portuguese Bible catalog holds book metadata only (names and chapter counts), which is factual
 * and translation-independent. Verse text is bundled separately.
 */
class PortugueseBibleCatalogTest {
    private val catalog = PortugueseBibleCatalog()

    @Test
    fun `has the 66 books of the protestant canon`() =
        runBlocking {
            assertEquals(66, catalog.books().size)
        }

    @Test
    fun `books are numbered 1 to 66 in canonical order`() =
        runBlocking {
            assertEquals((1..66).toList(), catalog.books().map { it.id })
        }

    @Test
    fun `total chapter count is 1189`() =
        runBlocking {
            assertEquals(1189, catalog.books().sumOf { it.chapterCount })
        }

    @Test
    fun `first book is Genesis with 50 chapters`() =
        runBlocking {
            val first = catalog.books().first()
            assertEquals("Gênesis", first.name)
            assertEquals(50, first.chapterCount)
        }

    @Test
    fun `last book is Revelation with 22 chapters`() =
        runBlocking {
            val last = catalog.books().last()
            assertEquals("Apocalipse", last.name)
            assertEquals(22, last.chapterCount)
        }

    @Test
    fun `Psalms has 150 chapters`() =
        runBlocking {
            val psalms = catalog.books().single { it.name == "Salmos" }
            assertEquals(150, psalms.chapterCount)
        }
}
