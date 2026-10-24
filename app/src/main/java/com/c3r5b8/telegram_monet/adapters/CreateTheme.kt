package com.c3r5b8.telegram_monet.adapters

import android.content.Context
import android.util.Log
import com.c3r5b8.telegram_monet.common.ColorScheme
import com.c3r5b8.telegram_monet.common.Constants
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Locale

fun createTheme(
    context: Context,
    isAmoled: Boolean,
    isGradient: Boolean,
    isAvatarGradient: Boolean,
    isNicknameColorful: Boolean,
    isAlterOutColor: Boolean,
    isUseDivider: Boolean,
    inputFileName: String,
    outputFileName: String,
    customSeedColor: Int = 0,
    customScheme: ColorScheme = ColorScheme.EXPRESSIVE,
    embeddedWallpaperBytes: ByteArray? = null,
) {
    val generatedThemeText = buildThemeContent(
        context = context,
        isAmoled = isAmoled,
        isGradient = isGradient,
        isAvatarGradient = isAvatarGradient,
        isNicknameColorful = isNicknameColorful,
        isAlterOutColor = isAlterOutColor,
        isUseDivider = isUseDivider,
        inputFileName = inputFileName,
        customSeedColor = customSeedColor,
        customScheme = customScheme,
    )

    val finalBytes = if (embeddedWallpaperBytes != null && embeddedWallpaperBytes.isNotEmpty()) {
        appendWallpaperToTheme(generatedThemeText, embeddedWallpaperBytes)
    } else {
        generatedThemeText.toByteArray(Charsets.UTF_8)
    }

    File(context.filesDir, outputFileName).writeBytes(finalBytes)
    shareTheme(context, outputFileName)
}

fun buildThemeBytes(
    context: Context,
    isAmoled: Boolean,
    isGradient: Boolean,
    isAvatarGradient: Boolean,
    isNicknameColorful: Boolean,
    isAlterOutColor: Boolean,
    isUseDivider: Boolean,
    inputFileName: String,
    customSeedColor: Int = 0,
    customScheme: ColorScheme = ColorScheme.EXPRESSIVE,
    embeddedWallpaperBytes: ByteArray? = null,
): ByteArray {
    val text = buildThemeContent(
        context = context,
        isAmoled = isAmoled,
        isGradient = isGradient,
        isAvatarGradient = isAvatarGradient,
        isNicknameColorful = isNicknameColorful,
        isAlterOutColor = isAlterOutColor,
        isUseDivider = isUseDivider,
        inputFileName = inputFileName,
        customSeedColor = customSeedColor,
        customScheme = customScheme,
    )
    return if (embeddedWallpaperBytes != null && embeddedWallpaperBytes.isNotEmpty()) {
        appendWallpaperToTheme(text, embeddedWallpaperBytes)
    } else {
        text.toByteArray(Charsets.UTF_8)
    }
}

fun appendWallpaperToTheme(themeText: String, wallpaperBytes: ByteArray): ByteArray {
    val base = themeText.trimEnd() + "\n"
    val placeholder = "wallpaperFileOffset=0000000000\n"
    val testOffset = (base + placeholder).toByteArray(Charsets.UTF_8).size
    val realHeader = String.format(Locale.US, "wallpaperFileOffset=%010d\n", testOffset)
    val textBytes = (base + realHeader).toByteArray(Charsets.UTF_8)

    val output = ByteArray(textBytes.size + wallpaperBytes.size)
    System.arraycopy(textBytes, 0, output, 0, textBytes.size)
    System.arraycopy(wallpaperBytes, 0, output, textBytes.size, wallpaperBytes.size)
    return output
}

fun buildThemeContent(
    context: Context,
    isAmoled: Boolean,
    isGradient: Boolean,
    isAvatarGradient: Boolean,
    isNicknameColorful: Boolean,
    isAlterOutColor: Boolean,
    isUseDivider: Boolean,
    inputFileName: String,
    customSeedColor: Int = 0,
    customScheme: ColorScheme = ColorScheme.EXPRESSIVE,
): String {
    val reader = BufferedReader(InputStreamReader(context.assets.open(inputFileName)))
    val listMain = reader.readLines().toMutableList()
    reader.close()

    if (isGradient) {
        listMain.forEachIndexed { index, value ->
            listMain[index] = value.replace("noGradient", "chat_outBubbleGradient")
        }
    }

    if (isAlterOutColor) {
        val inputSecondFileName =
            if (inputFileName == Constants.INPUT_FILE_TELEGRAM_LIGHT) {
                Constants.INPUT_FILE_TELEGRAM_DARK
            } else {
                Constants.INPUT_FILE_TELEGRAM_LIGHT
            }
        val reader2 = BufferedReader(InputStreamReader(context.assets.open(inputSecondFileName)))
        val listSecond = reader2.readLines()
        reader2.close()

        ListToReplaceNewThemeTelegram.forEach { value ->
            val index1 = listMain.indexOfFirst { it.startsWith("$value=") }
            val index2 = listSecond.indexOfFirst { it.startsWith("$value=") }
            if (index1 >= 0 && index2 >= 0) {
                listMain[index1] = listSecond[index2]
            } else {
                Log.d("TGMonetRadical", "Alternate color mapping missing key: $value ($index1, $index2)")
            }
        }
    }

    var themeImport = listMain.joinToString("\n") + "\n"

    if (isUseDivider) {
        themeImport = themeImport
            .replace("divider=n1_50", "divider=n1_200")
            .replace("divider=n1_900", "divider=n1_700")
    }

    if (isNicknameColorful) {
        themeImport += "\navatar_nameInMessageBlue=a1_400\n" +
                "avatar_nameInMessageCyan=a1_400\n" +
                "avatar_nameInMessageGreen=a1_400\n" +
                "avatar_nameInMessageOrange=a1_400\n" +
                "avatar_nameInMessagePink=a1_400\n" +
                "avatar_nameInMessageRed=a1_400\n" +
                "avatar_nameInMessageViolet=a1_400\n"
    }

    if (isAvatarGradient) {
        themeImport = themeImport
            .replace("avatar_backgroundBlue=n2_800", "avatar_backgroundBlue=n2_700")
            .replace("avatar_backgroundCyan=n2_800", "avatar_backgroundCyan=n2_700")
            .replace("avatar_backgroundGreen=n2_800", "avatar_backgroundGreen=n2_700")
            .replace("avatar_backgroundOrange=n2_800", "avatar_backgroundOrange=n2_700")
            .replace("avatar_backgroundPink=n2_800", "avatar_backgroundPink=n2_700")
            .replace("avatar_backgroundRed=n2_800", "avatar_backgroundRed=n2_700")
            .replace("avatar_backgroundSaved=n2_800", "avatar_backgroundSaved=n2_700")
            .replace("avatar_backgroundViolet=n2_800", "avatar_backgroundViolet=n2_700")
    }

    if (isAmoled) {
        themeImport = themeImport.replace("n1_900", "n1_1000")
    }

    val customColors = if (customSeedColor != 0) {
        generateMonetColors(customSeedColor, customScheme)
    } else {
        emptyMap()
    }

    return changeTextTelegram(themeImport, context, customColors)
}
