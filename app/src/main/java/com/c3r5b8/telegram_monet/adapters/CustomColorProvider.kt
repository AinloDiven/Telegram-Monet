package com.c3r5b8.telegram_monet.adapters

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import com.c3r5b8.telegram_monet.R
import com.c3r5b8.telegram_monet.common.ColorScheme
import hct.Hct
import scheme.SchemeContent
import scheme.SchemeExpressive
import scheme.SchemeFidelity
import scheme.SchemeFruitSalad
import scheme.SchemeMonochrome
import scheme.SchemeNeutral
import scheme.SchemeRainbow
import scheme.SchemeTonalSpot
import scheme.SchemeVibrant

val PALETTE_TONES = listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 95, 99, 100)
private val ANDROID_TO_HCT_TONE = PALETTE_TONES.associateBy { 1000 - it * 10 }

private const val DEFAULT_CONTRAST = 0.0

fun schemeFor(
	argb: Int,
	colorScheme: ColorScheme,
	isDark: Boolean = false,
) =
    Hct.fromInt(argb).let { hct ->
        when (colorScheme) {
            ColorScheme.TONAL_SPOT -> SchemeTonalSpot(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.FIDELITY -> SchemeFidelity(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.VIBRANT -> SchemeVibrant(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.NEUTRAL -> SchemeNeutral(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.EXPRESSIVE -> SchemeExpressive(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.FRUIT_SALAD -> SchemeFruitSalad(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.RAINBOW -> SchemeRainbow(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.CONTENT -> SchemeContent(hct, isDark, DEFAULT_CONTRAST)
            ColorScheme.MONOCHROME -> SchemeMonochrome(hct, isDark, DEFAULT_CONTRAST)
        }
    }

fun generateMonetColors(
    seedArgb: Int,
    colorScheme: ColorScheme = ColorScheme.EXPRESSIVE,
): Map<String, Int> {
    val scheme = schemeFor(seedArgb, colorScheme)
    val palettes = mapOf(
        "a1" to scheme.primaryPalette,
        "a2" to scheme.secondaryPalette,
        "a3" to scheme.tertiaryPalette,
        "n1" to scheme.neutralPalette,
        "n2" to scheme.neutralVariantPalette,
    )
    return buildMap {
        palettes.forEach { (prefix, palette) ->
            ANDROID_TO_HCT_TONE.forEach { (androidTone, hctTone) ->
                put("${prefix}_${androidTone}", palette.tone(hctTone))
            }
        }
    }
}

fun getSystemMonetColors(context: Context): Map<String, Int> {
    return mapOf(
        "a1_0" to getColor(context, R.color.system_accent1_0),
        "a1_10" to getColor(context, R.color.system_accent1_10),
        "a1_50" to getColor(context, R.color.system_accent1_50),
        "a1_100" to getColor(context, R.color.system_accent1_100),
        "a1_200" to getColor(context, R.color.system_accent1_200),
        "a1_300" to getColor(context, R.color.system_accent1_300),
        "a1_400" to getColor(context, R.color.system_accent1_400),
        "a1_500" to getColor(context, R.color.system_accent1_500),
        "a1_600" to getColor(context, R.color.system_accent1_600),
        "a1_700" to getColor(context, R.color.system_accent1_700),
        "a1_800" to getColor(context, R.color.system_accent1_800),
        "a1_900" to getColor(context, R.color.system_accent1_900),
        "a1_1000" to getColor(context, R.color.system_accent1_1000),

        "a2_0" to getColor(context, R.color.system_accent2_0),
        "a2_10" to getColor(context, R.color.system_accent2_10),
        "a2_50" to getColor(context, R.color.system_accent2_50),
        "a2_100" to getColor(context, R.color.system_accent2_100),
        "a2_200" to getColor(context, R.color.system_accent2_200),
        "a2_300" to getColor(context, R.color.system_accent2_300),
        "a2_400" to getColor(context, R.color.system_accent2_400),
        "a2_500" to getColor(context, R.color.system_accent2_500),
        "a2_600" to getColor(context, R.color.system_accent2_600),
        "a2_700" to getColor(context, R.color.system_accent2_700),
        "a2_800" to getColor(context, R.color.system_accent2_800),
        "a2_900" to getColor(context, R.color.system_accent2_900),
        "a2_1000" to getColor(context, R.color.system_accent2_1000),

        "a3_0" to getColor(context, R.color.system_accent3_0),
        "a3_10" to getColor(context, R.color.system_accent3_10),
        "a3_50" to getColor(context, R.color.system_accent3_50),
        "a3_100" to getColor(context, R.color.system_accent3_100),
        "a3_200" to getColor(context, R.color.system_accent3_200),
        "a3_300" to getColor(context, R.color.system_accent3_300),
        "a3_400" to getColor(context, R.color.system_accent3_400),
        "a3_500" to getColor(context, R.color.system_accent3_500),
        "a3_600" to getColor(context, R.color.system_accent3_600),
        "a3_700" to getColor(context, R.color.system_accent3_700),
        "a3_800" to getColor(context, R.color.system_accent3_800),
        "a3_900" to getColor(context, R.color.system_accent3_900),
        "a3_1000" to getColor(context, R.color.system_accent3_1000),

        "n1_0" to getColor(context, R.color.system_neutral1_0),
        "n1_10" to getColor(context, R.color.system_neutral1_10),
        "n1_50" to getColor(context, R.color.system_neutral1_50),
        "n1_100" to getColor(context, R.color.system_neutral1_100),
        "n1_200" to getColor(context, R.color.system_neutral1_200),
        "n1_300" to getColor(context, R.color.system_neutral1_300),
        "n1_400" to getColor(context, R.color.system_neutral1_400),
        "n1_500" to getColor(context, R.color.system_neutral1_500),
        "n1_600" to getColor(context, R.color.system_neutral1_600),
        "n1_700" to getColor(context, R.color.system_neutral1_700),
        "n1_800" to getColor(context, R.color.system_neutral1_800),
        "n1_900" to getColor(context, R.color.system_neutral1_900),
        "n1_1000" to getColor(context, R.color.system_neutral1_1000),

        "n2_0" to getColor(context, R.color.system_neutral2_0),
        "n2_10" to getColor(context, R.color.system_neutral2_10),
        "n2_50" to getColor(context, R.color.system_neutral2_50),
        "n2_100" to getColor(context, R.color.system_neutral2_100),
        "n2_200" to getColor(context, R.color.system_neutral2_200),
        "n2_300" to getColor(context, R.color.system_neutral2_300),
        "n2_400" to getColor(context, R.color.system_neutral2_400),
        "n2_500" to getColor(context, R.color.system_neutral2_500),
        "n2_600" to getColor(context, R.color.system_neutral2_600),
        "n2_700" to getColor(context, R.color.system_neutral2_700),
        "n2_800" to getColor(context, R.color.system_neutral2_800),
        "n2_900" to getColor(context, R.color.system_neutral2_900),
        "n2_1000" to getColor(context, R.color.system_neutral2_1000),

        "monetRedDark" to getColor(context, R.color.monetRedDark),
        "monetRedLight" to getColor(context, R.color.monetRedLight),
        "monetRedCall" to getColor(context, R.color.colorCallRed),
        "monetGreenCall" to getColor(context, R.color.colorCallGreen),
    )
}
