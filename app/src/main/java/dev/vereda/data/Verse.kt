package dev.vereda.data

import androidx.room.Entity

/** A single Bible verse of the bundled translation, keyed by book, chapter and verse number. */
@Entity(tableName = "verse", primaryKeys = ["bookId", "chapter", "verse"])
data class Verse(
    val bookId: Int,
    val chapter: Int,
    val verse: Int,
    val text: String,
)
