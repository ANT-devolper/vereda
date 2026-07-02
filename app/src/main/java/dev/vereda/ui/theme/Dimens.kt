package dev.vereda.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Spacing tokens shared across screens, so paddings and gaps stay consistent
 * (they used to vary 16/20/24 ad hoc).
 */
object Dimens {
    /** Outer padding around a screen's content. */
    val screenPadding = 24.dp

    /** Inner padding of a card. */
    val cardPadding = 20.dp

    /** Standard gap between stacked sections. */
    val gap = 16.dp

    /** Tight gap between closely related elements. */
    val gapSmall = 8.dp

    /** Generous gap between major blocks (e.g. hero cards). */
    val gapLarge = 24.dp
}
