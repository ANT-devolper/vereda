package dev.vereda.progress

/**
 * The 66 books of the Protestant canon with their Portuguese names and chapter counts.
 *
 * This is factual, translation-independent metadata (it feeds reading progress and navigation).
 * Verse text comes from the bundled Bible database, not from here.
 */
class PortugueseBibleCatalog : BibleCatalog {
    override suspend fun books(): List<BibleBook> = BOOKS

    private companion object {
        val BOOKS: List<BibleBook> =
            listOf(
                // Old Testament
                "Gênesis" to 50,
                "Êxodo" to 40,
                "Levítico" to 27,
                "Números" to 36,
                "Deuteronômio" to 34,
                "Josué" to 24,
                "Juízes" to 21,
                "Rute" to 4,
                "1 Samuel" to 31,
                "2 Samuel" to 24,
                "1 Reis" to 22,
                "2 Reis" to 25,
                "1 Crônicas" to 29,
                "2 Crônicas" to 36,
                "Esdras" to 10,
                "Neemias" to 13,
                "Ester" to 10,
                "Jó" to 42,
                "Salmos" to 150,
                "Provérbios" to 31,
                "Eclesiastes" to 12,
                "Cânticos" to 8,
                "Isaías" to 66,
                "Jeremias" to 52,
                "Lamentações" to 5,
                "Ezequiel" to 48,
                "Daniel" to 12,
                "Oseias" to 14,
                "Joel" to 3,
                "Amós" to 9,
                "Obadias" to 1,
                "Jonas" to 4,
                "Miqueias" to 7,
                "Naum" to 3,
                "Habacuque" to 3,
                "Sofonias" to 3,
                "Ageu" to 2,
                "Zacarias" to 14,
                "Malaquias" to 4,
                // New Testament
                "Mateus" to 28,
                "Marcos" to 16,
                "Lucas" to 24,
                "João" to 21,
                "Atos" to 28,
                "Romanos" to 16,
                "1 Coríntios" to 16,
                "2 Coríntios" to 13,
                "Gálatas" to 6,
                "Efésios" to 6,
                "Filipenses" to 4,
                "Colossenses" to 4,
                "1 Tessalonicenses" to 5,
                "2 Tessalonicenses" to 3,
                "1 Timóteo" to 6,
                "2 Timóteo" to 4,
                "Tito" to 3,
                "Filemom" to 1,
                "Hebreus" to 13,
                "Tiago" to 5,
                "1 Pedro" to 5,
                "2 Pedro" to 3,
                "1 João" to 5,
                "2 João" to 1,
                "3 João" to 1,
                "Judas" to 1,
                "Apocalipse" to 22,
            ).mapIndexed { index, (name, chapters) ->
                BibleBook(id = index + 1, name = name, chapterCount = chapters)
            }
    }
}
