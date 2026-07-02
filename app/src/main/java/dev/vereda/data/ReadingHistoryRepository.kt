package dev.vereda.data

import dev.vereda.history.HistoryCalculator
import dev.vereda.history.HistoryDay
import dev.vereda.progress.BibleCatalog
import java.time.Clock

/** Exposes the reading history: completed chapters grouped by the local day they were read. */
interface ReadingHistoryRepository {
    /** Completed chapters grouped by day, most-recent day first. */
    suspend fun history(): List<HistoryDay>
}

/**
 * Room-backed [ReadingHistoryRepository], derived from the recorded `chapter_read` rows.
 *
 * The [clock] provides the zone used to turn each read instant into a local day, so grouping is
 * deterministic in tests; [calculator] holds the grouping rules.
 */
class DefaultReadingHistoryRepository(
    private val dao: ChapterReadDao,
    private val catalog: BibleCatalog,
    private val calculator: HistoryCalculator = HistoryCalculator(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : ReadingHistoryRepository {
    override suspend fun history(): List<HistoryDay> {
        val bookNames = catalog.books().associate { it.id to it.name }
        return calculator.group(dao.allReads(), bookNames, clock.zone)
    }
}
