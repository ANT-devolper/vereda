#!/usr/bin/env python3
"""Build the bundled Bible SQLite database from an eBible VPL (verse-per-line) text file.

Source: "Bíblia Livre" (porbr2018), CC BY 4.0 — see app assets/about and the README attribution.

Usage:
    python3 tools/build_bible_db.py <input_vpl.txt> <output.db> [--room-schema schema.json]

Without --room-schema it produces a plain `verse` table (for validation). With a Room schema JSON
it creates the table using Room's exact CREATE statement and adds room_master_table so the database
can be loaded via Room.createFromAsset.
"""
import argparse
import json
import re
import sqlite3
import sys

# USFM book code -> canonical id (1..66), matching PortugueseBibleCatalog order.
BOOK_IDS = {
    "GEN": 1, "EXO": 2, "LEV": 3, "NUM": 4, "DEU": 5, "JOS": 6, "JDG": 7, "RUT": 8,
    "1SA": 9, "2SA": 10, "1KI": 11, "2KI": 12, "1CH": 13, "2CH": 14, "EZR": 15, "NEH": 16,
    "EST": 17, "JOB": 18, "PSA": 19, "PRO": 20, "ECC": 21, "SOL": 22, "ISA": 23, "JER": 24,
    "LAM": 25, "EZE": 26, "DAN": 27, "HOS": 28, "JOE": 29, "AMO": 30, "OBA": 31, "JON": 32,
    "MIC": 33, "NAH": 34, "HAB": 35, "ZEP": 36, "HAG": 37, "ZEC": 38, "MAL": 39, "MAT": 40,
    "MAR": 41, "LUK": 42, "JOH": 43, "ACT": 44, "ROM": 45, "1CO": 46, "2CO": 47, "GAL": 48,
    "EPH": 49, "PHI": 50, "COL": 51, "1TH": 52, "2TH": 53, "1TI": 54, "2TI": 55, "TIT": 56,
    "PHM": 57, "HEB": 58, "JAM": 59, "1PE": 60, "2PE": 61, "1JO": 62, "2JO": 63, "3JO": 64,
    "JUD": 65, "REV": 66,
}

LINE = re.compile(r"^(\S+)\s+(\d+):(\d+)(?:-\d+)?\s+(.*)$")


def parse_rows(path):
    rows = []
    with open(path, encoding="utf-8") as f:
        for raw in f:
            line = raw.rstrip("\n")
            if not line.strip():
                continue
            m = LINE.match(line)
            if not m:
                raise ValueError(f"Unparseable line: {line!r}")
            code, chapter, verse, text = m.groups()
            book_id = BOOK_IDS.get(code)
            if book_id is None:
                raise ValueError(f"Unknown book code: {code!r}")
            rows.append((book_id, int(chapter), int(verse), text.strip()))
    return rows


def build(rows, out_path, room_schema=None):
    conn = sqlite3.connect(out_path)
    cur = conn.cursor()
    if room_schema:
        with open(room_schema, encoding="utf-8") as f:
            schema = json.load(f)
        entities = schema["database"]["entities"]
        verse = next(e for e in entities if e["tableName"] == "verse")
        cur.execute(verse["createSql"].replace("${TABLE_NAME}", "verse"))
        cur.execute(
            "CREATE TABLE room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)"
        )
        cur.execute(
            "INSERT INTO room_master_table (id, identity_hash) VALUES (42, ?)",
            (schema["database"]["identityHash"],),
        )
        cur.execute(f"PRAGMA user_version = {schema['database']['version']}")
    else:
        cur.execute(
            "CREATE TABLE verse (bookId INTEGER NOT NULL, chapter INTEGER NOT NULL, "
            "verse INTEGER NOT NULL, text TEXT NOT NULL, "
            "PRIMARY KEY(bookId, chapter, verse))"
        )
    cur.executemany("INSERT INTO verse (bookId, chapter, verse, text) VALUES (?, ?, ?, ?)", rows)
    conn.commit()

    # Validation summary.
    books = cur.execute("SELECT COUNT(DISTINCT bookId) FROM verse").fetchone()[0]
    chapters = cur.execute(
        "SELECT COUNT(*) FROM (SELECT DISTINCT bookId, chapter FROM verse)"
    ).fetchone()[0]
    verses = cur.execute("SELECT COUNT(*) FROM verse").fetchone()[0]
    conn.close()
    return books, chapters, verses


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("input")
    ap.add_argument("output")
    ap.add_argument("--room-schema")
    args = ap.parse_args()

    rows = parse_rows(args.input)
    books, chapters, verses = build(rows, args.output, args.room_schema)
    print(f"books={books} chapters={chapters} verses={verses}")
    if books != 66 or chapters != 1189:
        print("VALIDATION FAILED: expected 66 books and 1189 chapters", file=sys.stderr)
        sys.exit(1)
    print("validation OK")


if __name__ == "__main__":
    main()
