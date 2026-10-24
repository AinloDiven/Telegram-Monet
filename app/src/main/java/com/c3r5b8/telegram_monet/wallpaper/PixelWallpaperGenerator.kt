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
    val primary: Int,
    val secondary: Int,
    val tertiary: Int,
    val accent: Int,
    val slate: Int,
    val vibrant: Int,
    val softDune: Int,
    val lightDune: Int,
    val yellowPop: Int,
    val circle: Int = secondary,
    val dot: Int = accent,
    val rect: Int = primary,
    val wave: Int = primary,
    val secondaryWave: Int = secondary,
)

object PixelWallpaperGenerator {

    fun resolveColors(
        colors: Map<String, Int>,
        isDark: Boolean,
        isAmoled: Boolean,
    ): ResolvedWallpaperColors {
        return if (!isDark) {
            val bg = colors["n1_100"] ?: colors["n1_50"] ?: 0xFFEFF4FA.toInt()
            val primary = colors["a1_300"] ?: colors["a1_400"] ?: 0xFF9BC3FF.toInt()
            val secondary = colors["a2_200"] ?: colors["a2_300"] ?: 0xFFBFD9FF.toInt()
            val tertiary = colors["a3_300"] ?: colors["a3_200"] ?: 0xFF79BBAD.toInt()
            val accent = colors["a3_500"] ?: colors["a3_600"] ?: 0xFFFF5449.toInt()
            val slate = colors["n2_200"] ?: colors["n2_300"] ?: 0xFFB8C7DA.toInt()
            val vibrant = colors["a1_500"] ?: colors["a1_600"] ?: 0xFF4385F4.toInt()
            val softDune = colors["a1_200"] ?: colors["a1_100"] ?: 0xFFFED9CD.toInt()
            val lightDune = colors["a2_100"] ?: colors["a2_50"] ?: 0xFFEBD5DB.toInt()
            val yellowPop = colors["a3_200"] ?: colors["a3_100"] ?: 0xFFF9F57F.toInt()
            ResolvedWallpaperColors(
                background = bg,
                primary = primary,
                secondary = secondary,
                tertiary = tertiary,
                accent = accent,
                slate = slate,
                vibrant = vibrant,
                softDune = softDune,
                lightDune = lightDune,
                yellowPop = yellowPop,
                circle = secondary,
                dot = accent,
                rect = primary,
                wave = primary,
                secondaryWave = secondary,
            )
        } else {
            val bg = if (isAmoled) {
                colors["n1_1000"] ?: 0xFF000000.toInt()
            } else {
                colors["n1_900"] ?: 0xFF141218.toInt()
            }
            val primary = colors["a1_700"] ?: colors["a1_800"] ?: 0xFF39293C.toInt()
            val secondary = colors["a2_800"] ?: colors["a2_900"] ?: 0xFF2A2030.toInt()
            val tertiary = colors["a3_700"] ?: colors["a3_800"] ?: 0xFF243B36.toInt()
            val accent = colors["a3_400"] ?: colors["a3_300"] ?: 0xFFFFB4AB.toInt()
            val slate = colors["n2_700"] ?: colors["n2_800"] ?: 0xFF352B3E.toInt()
            val vibrant = colors["a1_400"] ?: colors["a1_300"] ?: 0xFFA8C7FA.toInt()
            val softDune = colors["a1_800"] ?: colors["a1_900"] ?: 0xFF4D2B28.toInt()
            val lightDune = colors["a2_900"] ?: colors["a2_800"] ?: 0xFF362534.toInt()
            val yellowPop = colors["a3_300"] ?: colors["a3_400"] ?: 0xFFD8D25F.toInt()
            ResolvedWallpaperColors(
                background = bg,
                primary = primary,
                secondary = secondary,
                tertiary = tertiary,
                accent = accent,
                slate = slate,
                vibrant = vibrant,
                softDune = softDune,
                lightDune = lightDune,
                yellowPop = yellowPop,
                circle = secondary,
                dot = accent,
                rect = primary,
                wave = primary,
                secondaryWave = secondary,
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
        val jitterX = (rng.nextFloat() - 0.5f) * 0.03f * w
        val jitterY = (rng.nextFloat() - 0.5f) * 0.03f * h
        val jitterAngle = (rng.nextFloat() - 0.5f) * 5.0f

        when (config.preset) {
            WallpaperPreset.PIXEL_MINIMAL -> renderPixelMinimal(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
            WallpaperPreset.PIXEL_BLOOM -> renderPixelBloom(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
            WallpaperPreset.SILK_DUNES -> renderSilkDunes(canvas, w, h, resolved, paint, jitterX, jitterY)
            WallpaperPreset.PIXEL_CLASSIC -> renderPixelClassic(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
            WallpaperPreset.FLUID_WAVES -> renderFluidWaves(canvas, w, h, resolved, paint, jitterX, jitterY)
            WallpaperPreset.BAUHAUS_MODERN -> renderBauhausModern(canvas, w, h, resolved, paint, jitterX, jitterY, jitterAngle)
        }
    }

    private fun drawRotatedSquircle(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        size: Float,
        cornerRad: Float,
        angleDeg: Float,
        paint: Paint,
    ) {
        canvas.save()
        canvas.rotate(angleDeg, cx, cy)
        val rectF = RectF(cx - size / 2f, cy - size / 2f, cx + size / 2f, cy + size / 2f)
        canvas.drawRoundRect(rectF, cornerRad, cornerRad, paint)
        canvas.restore()
    }

    private fun drawScallopedBadge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        baseRadius: Float,
        amplitude: Float,
        lobes: Int,
        paint: Paint,
    ) {
        val path = Path()
        val stepCount = lobes * 8
        for (i in 0 until stepCount) {
            val theta = (i * 2.0 * Math.PI / stepCount).toFloat()
            val r = baseRadius + amplitude * kotlin.math.cos((lobes * theta).toDouble()).toFloat()
            val x = cx + r * kotlin.math.cos(theta.toDouble()).toFloat()
            val y = cy + r * kotlin.math.sin(theta.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawOrganicCloud(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        paint: Paint,
    ) {
        val path = Path().apply {
            moveTo(cx - rx * 0.7f, cy)
            cubicTo(
                cx - rx * 0.9f, cy - ry * 0.7f,
                cx - rx * 0.2f, cy - ry * 0.9f,
                cx + rx * 0.1f, cy - ry * 0.8f
            )
            cubicTo(
                cx + rx * 0.7f, cy - ry * 0.9f,
                cx + rx * 1.0f, cy - ry * 0.3f,
                cx + rx * 0.8f, cy + ry * 0.2f
            )
            cubicTo(
                cx + rx * 0.9f, cy + ry * 0.8f,
                cx + rx * 0.2f, cy + ry * 0.9f,
                cx - rx * 0.2f, cy + ry * 0.7f
            )
            cubicTo(
                cx - rx * 0.8f, cy + ry * 0.9f,
                cx - rx * 1.0f, cy + ry * 0.3f,
                cx - rx * 0.7f, cy
            )
            close()
        }
        canvas.drawPath(path, paint)
    }

    private fun renderPixelMinimal(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
        jAngle: Float,
    ) {
        // 1. Top left rounded diamond (primary)
        paint.color = colors.primary
        drawRotatedSquircle(
            canvas = canvas,
            cx = 0.22f * w + jx,
            cy = 0.16f * h + jy,
            size = 0.34f * w,
            cornerRad = 0.11f * w,
            angleDeg = 45f + jAngle,
            paint = paint
        )

        // 2. Mid left green diamond peaking in (tertiary)
        paint.color = colors.tertiary
        drawRotatedSquircle(
            canvas = canvas,
            cx = 0f + jx * 0.5f,
            cy = 0.52f * h + jy,
            size = 0.28f * w,
            cornerRad = 0.09f * w,
            angleDeg = 45f - jAngle,
            paint = paint
        )

        // 3. Mid right soft organic cloud (secondary)
        paint.color = colors.secondary
        drawOrganicCloud(
            canvas = canvas,
            cx = 0.70f * w + jx,
            cy = 0.44f * h + jy,
            rx = 0.36f * w,
            ry = 0.22f * h,
            paint = paint
        )

        // 4. Bottom slate diamond (slate)
        paint.color = colors.slate
        drawRotatedSquircle(
            canvas = canvas,
            cx = 0.36f * w + jx,
            cy = 0.86f * h + jy,
            size = 0.42f * w,
            cornerRad = 0.14f * w,
            angleDeg = 45f + jAngle * 0.5f,
            paint = paint
        )

        // 5. Punchy accent dot (accent)
        paint.color = colors.accent
        canvas.drawCircle(0.68f * w + jx * 0.6f, 0.24f * h + jy * 0.6f, 0.030f * w, paint)
    }

    private fun renderPixelBloom(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
        jAngle: Float,
    ) {
        // 1. Top dome arch (secondary)
        paint.color = colors.secondary
        val archPath = Path().apply {
            moveTo(0.35f * w + jx, 0f)
            cubicTo(
                0.40f * w + jx, 0.16f * h + jy,
                0.80f * w, 0.28f * h + jy,
                w, 0.22f * h
            )
            lineTo(w, 0f)
            close()
        }
        canvas.drawPath(archPath, paint)

        // 2. Left vertical flowing wavy contour (tertiary)
        paint.color = colors.tertiary
        val leftBean = Path().apply {
            moveTo(0f, 0.20f * h + jy)
            cubicTo(
                0.16f * w + jx, 0.28f * h + jy,
                0.24f * w + jx, 0.45f * h,
                0.14f * w + jx, 0.62f * h + jy
            )
            cubicTo(
                0.08f * w + jx, 0.70f * h,
                0f, 0.78f * h + jy,
                0f, 0.82f * h
            )
            close()
        }
        canvas.drawPath(leftBean, paint)

        // 3. Pixel Signature 12-lobed Scalloped Flower Badge (vibrant)
        paint.color = colors.vibrant
        drawScallopedBadge(
            canvas = canvas,
            cx = 0.80f * w + jx,
            cy = 0.84f * h + jy,
            baseRadius = 0.28f * w,
            amplitude = 0.024f * w,
            lobes = 12,
            paint = paint
        )

        // 4. Accent dot
        paint.color = colors.accent
        canvas.drawCircle(0.42f * w + jx, 0.42f * h + jy, 0.032f * w, paint)
    }

    private fun renderSilkDunes(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        jx: Float,
        jy: Float,
    ) {
        // 1. Top-right soft lilac crest
        paint.color = colors.lightDune
        val crest = Path().apply {
            moveTo(0.55f * w + jx, 0f)
            cubicTo(
                0.70f * w + jx, 0.08f * h + jy,
                0.85f * w, 0.18f * h + jy,
                w, 0.24f * h
            )
            lineTo(w, 0f)
            close()
        }
        canvas.drawPath(crest, paint)

        // 2. Main upper dune ribbon (coral / primary)
        paint.color = colors.primary
        val upperDune = Path().apply {
            moveTo(0f, 0.08f * h + jy)
            cubicTo(
                0.30f * w + jx, 0.16f * h + jy,
                0.65f * w + jx, 0.38f * h,
                w, 0.52f * h + jy
            )
            lineTo(w, 0.64f * h + jy)
            cubicTo(
                0.60f * w + jx, 0.48f * h,
                0.25f * w + jx, 0.30f * h + jy,
                0f, 0.34f * h + jy
            )
            close()
        }
        canvas.drawPath(upperDune, paint)

        // 3. Lower dune ribbon (warm apricot / softDune)
        paint.color = colors.softDune
        val lowerDune = Path().apply {
            moveTo(0f, 0.42f * h + jy)
            cubicTo(
                0.35f * w + jx, 0.52f * h + jy,
                0.60f * w + jx, 0.72f * h,
                w, 0.88f * h + jy
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        canvas.drawPath(lowerDune, paint)

        // 4. Sunny corner pop accent (bottom-left)
        paint.color = colors.yellowPop
        val sunPop = Path().apply {
            moveTo(0f, 0.74f * h + jy)
            cubicTo(
                0.12f * w + jx, 0.78f * h + jy,
                0.18f * w + jx, 0.88f * h,
                0.08f * w, h
            )
            lineTo(0f, h)
            close()
        }
        canvas.drawPath(sunPop, paint)
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
        // 1. Center Circle (scaled down from 0.148w to 0.115w)
        paint.color = colors.circle
        val cx = 0.50f * w + jx
        val cy = 0.403f * h + jy
        val cr = 0.115f * w
        canvas.drawCircle(cx, cy, cr, paint)

        // 2. Punchy Accent Dot (scaled down from 0.045w to 0.032w)
        paint.color = colors.dot
        val dx = 0.680f * w + jx * 0.8f
        val dy = 0.330f * h + jy * 0.8f
        val dr = 0.032f * w
        canvas.drawCircle(dx, dy, dr, paint)

        // 3. Tilted Rounded Rectangle (scaled down from 0.33w x 0.40h to 0.25w x 0.30h)
        paint.color = colors.rect
        val rx = 0.85f * w + jx
        val ry = 0.540f * h + jy
        val rw = 0.25f * w
        val rh = 0.30f * h
        val cornerRadius = 0.07f * w
        val baseAngle = -4.1f + jAngle

        canvas.save()
        canvas.rotate(baseAngle, rx, ry)
        val rectF = RectF(rx - rw / 2f, ry - rh / 2f, rx + rw / 2f, ry + rh / 2f)
        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paint)
        canvas.restore()

        // 4. Flowing Organic Bezier Wave (scaled down to leave more space)
        paint.color = colors.wave
        val path = Path().apply {
            moveTo(0f, 0.48f * h + jy * 0.5f)
            cubicTo(
                0.12f * w, 0.52f * h + jy,
                0.36f * w + jx, 0.56f * h + jy,
                0.38f * w + jx, 0.66f * h + jy
            )
            cubicTo(
                0.38f * w + jx, 0.74f * h + jy,
                0.46f * w + jx, 0.80f * h + jy,
                0.45f * w + jx, 0.88f * h + jy
            )
            cubicTo(
                0.44f * w + jx, 0.94f * h,
                0.35f * w + jx, 0.98f * h,
                0.28f * w + jx, 1.00f * h
            )
            lineTo(0f, h)
            lineTo(0f, 0.48f * h + jy * 0.5f)
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
            moveTo(w, 0.18f * h + jy)
            cubicTo(
                0.78f * w + jx, 0.22f * h,
                0.58f * w + jx, 0.14f * h,
                0.46f * w + jx, 0.28f * h + jy
            )
            cubicTo(
                0.35f * w + jx, 0.40f * h,
                0.60f * w, 0.50f * h + jy,
                0.85f * w, 0.54f * h
            )
            lineTo(w, 0.54f * h)
            lineTo(w, 0.18f * h + jy)
            close()
        }
        canvas.drawPath(pathSec, paint)

        // Accent Circle (scaled down from 0.18w to 0.125w)
        paint.color = colors.circle
        canvas.drawCircle(0.32f * w + jx, 0.38f * h + jy, 0.125f * w, paint)

        // Punchy Dot (scaled down from 0.05w to 0.035w)
        paint.color = colors.dot
        canvas.drawCircle(0.70f * w + jx, 0.25f * h + jy, 0.035f * w, paint)

        // Primary bottom flowing wave
        paint.color = colors.wave
        val pathMain = Path().apply {
            moveTo(0f, 0.55f * h + jy)
            cubicTo(
                0.22f * w + jx, 0.58f * h,
                0.40f * w + jx, 0.72f * h,
                0.32f * w + jx, 0.82f * h + jy
            )
            cubicTo(
                0.26f * w + jx, 0.90f * h,
                0.48f * w + jx, 0.96f * h,
                0.58f * w + jx, 1.00f * h
            )
            lineTo(0f, h)
            lineTo(0f, 0.55f * h + jy)
            close()
        }
        canvas.drawPath(pathMain, paint)

        // Floating pill on bottom right (scaled down from 0.28w x 0.35h to 0.20w x 0.25h)
        paint.color = colors.rect
        val pillX = 0.82f * w + jx
        val pillY = 0.75f * h + jy
        val pillW = 0.20f * w
        val pillH = 0.25f * h
        canvas.save()
        canvas.rotate(-12f, pillX, pillY)
        canvas.drawRoundRect(
            RectF(pillX - pillW / 2f, pillY - pillH / 2f, pillX + pillW / 2f, pillY + pillH / 2f),
            0.09f * w, 0.09f * w, paint
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
        // Large background circle (scaled down from 0.32w to 0.22w)
        paint.color = colors.secondaryWave
        canvas.drawCircle(0.72f * w + jx, 0.32f * h + jy, 0.22f * w, paint)

        // Tall slender pill (scaled down from 0.36w x 0.52h to 0.22w x 0.36h)
        paint.color = colors.rect
        val px = 0.32f * w + jx
        val py = 0.48f * h + jy
        val pw = 0.22f * w
        val ph = 0.36f * h
        canvas.save()
        canvas.rotate(8f + jAngle, px, py)
        canvas.drawRoundRect(
            RectF(px - pw / 2f, py - ph / 2f, px + pw / 2f, py + ph / 2f),
            0.11f * w, 0.11f * w, paint
        )
        canvas.restore()

        // Overlapping middle circle (scaled down from 0.20w to 0.14w)
        paint.color = colors.circle
        canvas.drawCircle(0.52f * w + jx, 0.58f * h + jy, 0.14f * w, paint)

        // Punchy accent dot (scaled down from 0.055w to 0.035w)
        paint.color = colors.dot
        canvas.drawCircle(0.26f * w + jx, 0.26f * h + jy, 0.035f * w, paint)

        // Bottom curved base (scaled down)
        paint.color = colors.wave
        val pathBase = Path().apply {
            moveTo(0f, 0.90f * h + jy)
            cubicTo(
                0.30f * w + jx, 0.86f * h,
                0.70f * w + jx, 0.94f * h,
                w, 0.90f * h + jy
            )
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        canvas.drawPath(pathBase, paint)
    }
}
