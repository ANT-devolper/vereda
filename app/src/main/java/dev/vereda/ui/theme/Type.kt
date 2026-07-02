package dev.vereda.ui.theme

import android.os.Build
import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.vereda.R

/**
 * Lora — a serif face used for reading and hero headings. Bundled as a single variable-weight
 * font (`res/font/lora_variable.ttf`); each weight is a variation instance.
 *
 * `FontVariation.Settings` (weight axis) applies on API 26+. On API 24–25 the variation is ignored
 * and Compose falls back to synthetic bolding, which is acceptable for the few emphasized styles.
 */
@OptIn(ExperimentalTextApi::class)
private fun loraFont(weight: FontWeight): Font =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Font(
            resId = R.font.lora_variable,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    } else {
        Font(resId = R.font.lora_variable, weight = weight)
    }

val Lora =
    FontFamily(
        loraFont(FontWeight.Normal),
        loraFont(FontWeight.Medium),
        loraFont(FontWeight.SemiBold),
        loraFont(FontWeight.Bold),
    )

// UI chrome stays on the system sans; Lora carries the reading body and the hero headings.
val Typography =
    Typography().run {
        copy(
            displayMedium =
                displayMedium.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
            displaySmall =
                displaySmall.copy(fontFamily = Lora, fontWeight = FontWeight.Bold),
            headlineMedium =
                headlineMedium.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
            headlineSmall =
                headlineSmall.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
            titleLarge =
                titleLarge.copy(fontFamily = Lora, fontWeight = FontWeight.SemiBold),
            // Reading body: Lora, larger and airier than the M3 default for long-form comfort.
            bodyLarge =
                bodyLarge.copy(
                    fontFamily = Lora,
                    fontSize = 18.sp,
                    lineHeight = 30.sp,
                    letterSpacing = 0.sp,
                ),
        )
    }

/** Explicit reading style for the Bible text, so verse rendering is independent of theme tweaks. */
val ReadingTextStyle: TextStyle =
    TextStyle(
        fontFamily = Lora,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
    )
