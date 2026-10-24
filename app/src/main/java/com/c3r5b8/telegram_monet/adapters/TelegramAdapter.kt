package com.c3r5b8.telegram_monet.adapters

import android.content.Context

fun changeTextTelegram(
    file: String,
    applicationContext: Context,
    customColors: Map<String, Int> = emptyMap(),
): String {
    val monetList = getSystemMonetColors(applicationContext).toMutableMap()
    monetList.putAll(customColors)

    // Frosted glass translucent color tokens
    // 0xD9 = ~85% alpha, 0xCC = ~80% alpha, 0xB3 = ~70% alpha, 0x80 = 50% alpha
    fun withAlpha(baseKey: String, alphaHex: Int): Int {
        val base = monetList[baseKey] ?: 0
        return (base and 0x00FFFFFF) or (alphaHex shl 24)
    }

    monetList["n1_900_glass_85"] = withAlpha("n1_900", 0xD9)
    monetList["n1_900_glass_80"] = withAlpha("n1_900", 0xCC)
    monetList["n1_900_glass_70"] = withAlpha("n1_900", 0xB3)

    monetList["n1_1000_glass_85"] = withAlpha("n1_1000", 0xD9)
    monetList["n1_1000_glass_80"] = withAlpha("n1_1000", 0xCC)

    monetList["n1_800_glass_85"] = withAlpha("n1_800", 0xD9)
    monetList["n1_800_glass_80"] = withAlpha("n1_800", 0xCC)

    monetList["n1_50_glass_85"] = withAlpha("n1_50", 0xD9)
    monetList["n1_50_glass_80"] = withAlpha("n1_50", 0xCC)
    monetList["n1_50_glass_70"] = withAlpha("n1_50", 0xB3)

    monetList["n1_100_glass_85"] = withAlpha("n1_100", 0xD9)
    monetList["a1_700_glass_85"] = withAlpha("a1_700", 0xD9)
    monetList["a1_200_glass_85"] = withAlpha("a1_200", 0xD9)
    monetList["a1_100_glass_85"] = withAlpha("a1_100", 0xD9)

    val a2_700_2 = withAlpha("a2_700", 0x80)
    val n2_100_2 = withAlpha("n2_100", 0x80)

    var themeText = file.replace("$", "")
    themeText = themeText.replace(Regex("chat_selectedBackground=a2_700"), "chat_selectedBackground=$a2_700_2")
    themeText = themeText.replace(Regex("chat_selectedBackground=n2_100"), "chat_selectedBackground=$n2_100_2")

    // Replace keys sorted by length descending so prefixes like n1_10 do not clobber n1_100, n1_1000, etc.
    val sortedKeys = monetList.keys.sortedByDescending { it.length }
    for (key in sortedKeys) {
        val value = monetList[key]!!
        themeText = themeText.replace(key, value.toString())
    }

    if (!themeText.contains("WLS=")) {
        themeText += "\nWLS=tg://wallpaper?mode=blur\n"
    }

    return themeText
}
