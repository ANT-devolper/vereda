package dev.vereda.history

import dev.vereda.data.ChapterRead
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/** A single completed chapter as shown in the history, with the local time it was completed. */
data class HistoryEntry(
    val bookId: Int,
    val chapter: Int,
    val bookName: String,
    val time: LocalTime,
)

/** All chapters completed on a single local day, newest days listed first by the calculator. */
data class HistoryDay(
    val date: LocalDate,
    val entries: List<HistoryEntry>,
)

/**
 * Groups completed chapters into a day-by-day reading history.
 *
 * The calculation is pure: callers pass the read rows, the book-name lookup and the [ZoneId] used to turn
 * each `firstReadAt` instant into a local date and time, so the result is deterministic and easy to test.
 * Days are ordered most-recent first; entries within a day are ordered by completion time ascending.
 */
class HistoryCalculator {
    fun group(
        reads: List<ChapterRead>,
        bookNames: Map<Int, String>,
        zone: ZoneId,
    ): List<HistoryDay> =
        reads
            .groupBy { read -> read.firstReadAt.atZone(zone).toLocalDate() }
            .toSortedMap(reverseOrder())
            .map { (date, dayReads) ->
                HistoryDay(
                    date = date,
                    entries =
                        dayReads
                            .map { read ->
                                HistoryEntry(
                                    bookId = read.bookId,
                                    chapter = read.chapter,
                                    bookName = bookNames[read.bookId] ?: "",
                                    time = read.firstReadAt.atZone(zone).toLocalTime(),
                                )
                            }.sortedBy { it.time },
                )
            }
}
