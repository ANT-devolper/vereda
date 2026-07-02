package dev.vereda.ui.theme

import androidx.compose.ui.graphics.Color

// Vereda brand palette — "serene blue + gold", dark-first.
// "Vereda" means path/trail: a calm, contemplative night palette with a warm gold accent
// reserved for the reading streak (the app's gamified heart).

/** Deep night background. */
val VeredaBackground = Color(0xFF0F1419)

/** Card surface, one step above the background. */
val VeredaSurface = Color(0xFF182028)

/** Secondary surface: chapter cells, tracks, subtle containers. */
val VeredaSurfaceVariant = Color(0xFF202A33)

/** Serene blue — primary: CTAs, progress, "chapter read". */
val VeredaBlue = Color(0xFF6BA3C7)

/** Text/icon color drawn on top of [VeredaBlue]. */
val VeredaOnBlue = Color(0xFF08131C)

/** Warm gold — accent: highlights, verse numbers, status. */
val VeredaGold = Color(0xFFF2C14E)

/** Text/icon color drawn on top of [VeredaGold]. */
val VeredaOnGold = Color(0xFF241B00)

/** Primary text on background/surface. */
val VeredaOnBackground = Color(0xFFE3E8ED)

/** Muted text, verse numbers, hairlines. */
val VeredaOutline = Color(0xFF5A6B78)
