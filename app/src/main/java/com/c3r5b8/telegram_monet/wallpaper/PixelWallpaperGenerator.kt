package com.c3r5b8.telegram_monet.wallpaper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.random.Random

data class ResolvedWallpaperColors(
    val background: Int,
    val circle: Int,
    val dot: Int,
    val rect: Int,
    val wave: Int,
    val secondaryWave: Int,
)

object PixelWallpaperGenerator {

    fun resolveColors(
        colors: Map<String, Int>,
        isDark: Boolean,
        isAmoled: Boolean,
    ): ResolvedWallpaperColors {
        return if (!isDark) {
            ResolvedWallpaperColors(
                background = colors["n1_100"] ?: colors["n1_50"] ?: 0xFFFFA19B.toInt(),
                circle = colors["a2_300"] ?: colors["a2_200"] ?: 0xFFFF8C6F.toInt(),
                dot = colors["a3_500"] ?: colors["a3_600"] ?: 0xFFFF3429.toInt(),
                rect = colors["a1_400"] ?: colors["a1_300"] ?: 0xFFFF6C72.toInt(),
                wave = colors["a1_400"] ?: colors["a1_300"] ?: 0xFFFF736C.toInt(),
                secondaryWave = colors["a2_200"] ?: colors["a2_100"] ?: 0xFFFFB29B.toInt(),
            )
        } else {
            val bg = if (isAmoled) {
                colors["n1_1000"] ?: 0xFF000000.toInt()
            } else {
                colors["n1_900"] ?: 0xFF141218.toInt()
            }
            ResolvedWallpaperColors(
                background = bg,
                circle = colors["a2_700"] ?: colors["a2_800"] ?: 0xFF352B3E.toInt(),
                dot = colors["a3_400"] ?: colors["a3_500"] ?: 0xFFFFB4AB.toInt(),
                rect = colors["a1_600"] ?: colors["a1_700"] ?: 0xFF4A394C.toInt(),
                wave = colors["a1_700"] ?: colors["a1_800"] ?: 0xFF39293C.toInt(),
                secondaryWave = colors["a2_800"] ?: 0xFF2A2030.toInt(),
            )
        }
    }

    fun renderToBitmap(
        width: Int,
        height: Int,
        colors: Map<String, Int>,
        config: WallpaperConfig,
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        renderToCanvas(canvas, width.toFloat(), height.toFloat(), colors, config)
        return bitmap
    }

    fun renderToDrawScope(
        drawScope: DrawScope,
        colors: Map<String, Int>,
        config: WallpaperConfig,
    ) {
        val w = drawScope.size.width
        val h = drawScope.size.height
        drawScope.drawIntoCanvas { canvas ->
            renderToCanvas(canvas.nativeCanvas, w, h, colors, config)
        }
    }

    fun renderToCanvas(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: Map<String, Int>,
        config: WallpaperConfig,
    ) {
        val resolved = resolveColors(colors, config.isDark, config.isAmoled)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        // Draw background
        paint.color = resolved.background
        canvas.drawRect(0f, 0f, w, h, paint)

        val rng = Random(config.seed)
        val jitterX = (rng.nextFloat() - 0.5f) * 0.04f * w
        val jitterY = (rng.nextFloat() - 0.5f) * 0.04f * h
        val jitterAngle = (rng.nextFloat() - 0.5f) * 6.0f

        when (config.preset) {
            WallpaperPreset.PIXEL_CLASSIC -> renderPixelClassic(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
            WallpaperPreset.FLUID_WAVES -> renderFluidWaves(canvas, w, h, resolved, paint, jitterX, jitterY)
            WallpaperPreset.BAUHAUS_MODERN -> renderBauhausModern(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
        }
    }

    private fun renderPixelClassic(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
        jAngle: Float,
    ) {
        // 1. Center Circle
        paint.color = colors.circle
        val cx = 0.50f * w + jx
        val cy = 0.403f * h + jy
        val cr = 0.148f * w
        canvas.drawCircle(cx, cy, cr, paint)

        // 2. Punchy Accent Dot
        paint.color = colors.dot
        val dx = 0.708f * w + jx * 0.8f
        val dy = 0.321f * h + jy * 0.8f
        val dr = 0.045f * w
        canvas.drawCircle(dx, dy, dr, paint)

        // 3. Tilted Rounded Rectangle
        paint.color = colors.rect
        val rx = 0.84f * w + jx
        val ry = 0.545f * h + jy
        val rw = 0.33f * w
        val rh = 0.40f * h
        val cornerRadius = 0.09f * w
        val baseAngle = -4.1f + jAngle

        canvas.save()
        canvas.rotate(baseAngle, rx, ry)
        val rectF = RectF(rx - rw / 2f, ry - rh / 2f, rx + rw / 2f, ry + rh / 2f)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        canvas.restore()

        // 4. Flowing Organic Bezier Wave
        paint.color = colors.wave
        val path = Path().apply {
            moveTo(0f, 0.40f * h + jy * 0.5f)
            cubicTo(
                0.16f * w, 0.44f * h + jy,
                0.48f * w + jx, 0.48f * h + jy,
                0.49f * w + jx, 0.60f * h + jy
            )
            cubicTo(
                0.48f * w + jx, 0.67f * h + jy,
                0.57f * w + jx, 0.73f * h + jy,
                0.57f * w + jx, 0.82f * h + jy
            )
            cubicTo(
                0.57f * w + jx, 0.90f * h,
                0.46f * w + jx, 0.97f * h,
                0.36f * w + jx, 1.00f * h
            )
            lineTo(0f, h)
            lineTo(0f, 0.40f * h + jy * 0.5f)
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun renderFluidWaves(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
    ) {
        // Background wave layer (secondary)
        paint.color = colors.secondaryWave
        val pathSec = Path().apply {
            moveTo(w, 0.15f * h + jy)
            cubicTo(
                0.75f * w + jx, 0.20f * h,
                0.50f * w + jx, 0.10f * h,
                0.35f * w + jx, 0.28f * h + jy
            )
            cubicTo(
                0.20f * w + jx, 0.45f * h,
                0.50f * w, 0.55f * h + jy,
                0.80f * w, 0.60f * h
            )
            lineTo(w, 0.60f * h)
            lineTo(w, 0.15f * h + jy)
            close()
        }
        canvas.drawPath(pathSec, paint)

        // Accent Circle
        paint.color = colors.circle
        canvas.drawCircle(0.32f * w + jx, 0.38f * h + jy, 0.18f * w, paint)

        // Punchy Dot
        paint.color = colors.dot
        canvas.drawCircle(0.72f * w + jx, 0.22f * h + jy, 0.05f * w, paint)

        // Primary bottom flowing wave
        paint.color = colors.wave
        val pathMain = Path().apply {
            moveTo(0f, 0.48f * h + jy)
            cubicTo(
                0.25f * w + jx, 0.52f * h,
                0.45f * w + jx, 0.68f * h,
                0.38f * w + jx, 0.80f * h + jy
            )
            cubicTo(
                0.32f * w + jx, 0.90f * h,
                0.55f * w + jx, 0.96f * h,
                0.65f * w + jx, 1.00f * h
            )
            lineTo(0f, h)
            lineTo(0f, 0.48f * h + jy)
            close()
        }
        canvas.drawPath(pathMain, paint)

        // Floating pill on bottom right
        paint.color = colors.rect
        val pillX = 0.80f * w + jx
        val pillY = 0.72f * h + jy
        val pillW = 0.28f * w
        val pillH = 0.35f * h
        canvas.save()
        canvas.rotate(-12f, pillX, pillY)
        canvas.drawRoundRect(
            RectF(pillX - pillW / 2f, pillY - pillH / 2f, pillX + pillW / 2f, pillY + pillH / 2f),
            0.12f * w, 0.12f * w, paint
        )
        canvas.restore()
    }

    private fun renderBauhausModern(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
        jAngle: Float,
    ) {
        // Large background circle
        paint.color = colors.secondaryWave
        canvas.drawCircle(0.75f * w + jx, 0.35f * h + jy, 0.32f * w, paint)

        // Tall pill
        paint.color = colors.rect
        val px = 0.30f * w + jx
        val py = 0.50f * h + jy
        val pw = 0.36f * w
        val ph = 0.52f * h
        canvas.save()
        canvas.rotate(8f + jAngle, px, py)
        canvas.drawRoundRect(
            RectF(px - pw / 2f, py - ph / 2f, px + pw / 2f, py + ph / 2f),
            0.18f * w, 0.18f * w, paint
        )
        canvas.restore()

        // Overlapping middle circle
        paint.color = colors.circle
        canvas.drawCircle(0.55f * w + jx, 0.62f * h + jy, 0.20f * w, paint)

        // Punchy accent dot
        paint.color = colors.dot
        canvas.drawCircle(0.24f * w + jx, 0.25f * h + jy, 0.055f * w, paint)

        // Bottom curved base
        paint.color = colors.wave
        val pathBase = Path().apply {
            moveTo(0f, 0.85f * h + jy)
            cubicTo(
                0.30f * w + jx, 0.80f * h,
                0.70f * w + jx, 0.92f * h,
                w, 0.86f * h + jy
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        canvas.drawPath(pathBase, paint)
    }
}
