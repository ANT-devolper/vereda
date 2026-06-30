package dev.vereda.progress

/**
 * Source of the Bible catalog (books and their chapter counts).
 * The MVP implementation will be backed by the bundled Bible database.
 */
interface BibleCatalog {
    suspend fun books(): List<BibleBook>
}
