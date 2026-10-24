package com.c3r5b8.telegram_monet.wallpaper

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import com.c3r5b8.telegram_monet.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WallpaperStorage {

    private const val SUBDIR = "TgMonet"

    fun saveWallpaperToStorage(
        context: Context,
        bitmap: Bitmap,
        isDark: Boolean,
        presetName: String,
    ) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val mode = if (isDark) "dark" else "light"
        val fileName = "Pixel_${presetName}_${mode}_$timestamp.png"

        val ok = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(context, bitmap, fileName)
        } else {
            saveToLegacy(bitmap, fileName)
        }

        val msg = if (ok) {
            context.getString(R.string.wallpaper_save_success, fileName)
        } else {
            context.getString(R.string.wallpaper_save_failed)
        }

        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveViaMediaStore(context: Context, bitmap: Bitmap, fileName: String): Boolean {
        return runCatching {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$SUBDIR"
                )
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: return@runCatching false
            resolver.openOutputStream(uri)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            } ?: return@runCatching false
            true
        }.getOrDefault(false)
    }

    private fun saveToLegacy(bitmap: Bitmap, fileName: String): Boolean {
        return runCatching {
            @Suppress("DEPRECATION")
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                SUBDIR
            )
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            file.outputStream().use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            true
        }.getOrDefault(false)
    }

    fun compressToJpegBytes(bitmap: Bitmap, quality: Int = 90): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
