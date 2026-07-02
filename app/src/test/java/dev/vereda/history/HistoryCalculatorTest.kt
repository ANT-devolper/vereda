package dev.vereda.history

import dev.vereda.data.ChapterRead
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

/**
 * History grouping rules:
 * - Chapters completed on the same local day are grouped under that day.
 * - Days are ordered most-recent first; entries within a day by completion time ascending.
 * - Book names are resolved from the provided lookup.
 * - The local day/time comes from the given zone, so instants near midnight land on the right day.
 */
class HistoryCalculatorTest {
    private val calculator = HistoryCalculator()
    private val zone = ZoneOffset.UTC
    private val names = mapOf(1 to "Gênesis", 2 to "Êxodo")

    private fun readAt(
        bookId: Int,
        chapter: Int,
        dateTime: LocalDateTime,
        zone: ZoneId = this.zone,
    ) = ChapterRead(bookId = bookId, chapter = chapter, firstReadAt = dateTime.atZone(zone).toInstant())

    @Test
    fun `no reads yields an empty history`() {
        assertEquals(emptyList<HistoryDay>(), calculator.group(emptyList(), names, zone))
    }

    @Test
    fun `chapters read on the same day are grouped under one day`() {
        val reads =
            listOf(
                readAt(1, 1, LocalDateTime.of(2026, 7, 1, 8, 0)),
                readAt(1, 2, LocalDateTime.of(2026, 7, 1, 20, 30)),
            )

        val history = calculator.group(reads, names, zone)

        assertEquals(1, history.size)
        assertEquals(LocalDate.of(2026, 7, 1), history[0].date)
        assertEquals(2, history[0].entries.size)
    }

    @Test
    fun `entries within a day are ordered by time ascending`() {
        val reads =
            listOf(
                readAt(1, 2, LocalDateTime.of(2026, 7, 1, 20, 30)),
                readAt(1, 1, LocalDateTime.of(2026, 7, 1, 8, 0)),
            )

        val entries = calculator.group(reads, names, zone).single().entries

        assertEquals(listOf(LocalTime.of(8, 0), LocalTime.of(20, 30)), entries.map { it.time })
        assertEquals(listOf(1, 2), entries.map { it.chapter })
    }

    @Test
    fun `days are ordered most recent first`() {
        val reads =
            listOf(
                readAt(1, 1, LocalDateTime.of(2026, 6, 29, 9, 0)),
                readAt(1, 2, LocalDateTime.of(2026, 7, 1, 9, 0)),
                readAt(2, 1, LocalDateTime.of(2026, 6, 30, 9, 0)),
            )

        val dates = calculator.group(reads, names, zone).map { it.date }

        assertEquals(
            listOf(LocalDate.of(2026, 7, 1), LocalDate.of(2026, 6, 30), LocalDate.of(2026, 6, 29)),
            dates,
        )
    }

    @Test
    fun `book names are resolved from the lookup`() {
        val reads = listOf(readAt(2, 3, LocalDateTime.of(2026, 7, 1, 10, 0)))

        val entry =
            calculator
                .group(reads, names, zone)
                .single()
                .entries
                .single()

        assertEquals("Êxodo", entry.bookName)
        assertEquals(2, entry.bookId)
        assertEquals(3, entry.chapter)
    }

    @Test
    fun `the zone decides which local day a near-midnight read belongs to`() {
        // 2026-07-01T02:00Z is still 2026-06-30 23:00 in UTC-3.
        val saoPaulo = ZoneId.of("America/Sao_Paulo")
        val read = ChapterRead(bookId = 1, chapter = 1, firstReadAt = Instant.parse("2026-07-01T02:00:00Z"))

        val utcDay = calculator.group(listOf(read), names, ZoneOffset.UTC).single().date
        val spDay = calculator.group(listOf(read), names, saoPaulo).single().date

        assertEquals(LocalDate.of(2026, 7, 1), utcDay)
        assertEquals(LocalDate.of(2026, 6, 30), spDay)
    }
}
