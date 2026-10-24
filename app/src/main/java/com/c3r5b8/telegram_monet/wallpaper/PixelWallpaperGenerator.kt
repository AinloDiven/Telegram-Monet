package com.c3r5b8.telegram_monet.wallpaper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
            val bg = colors["n1_50"] ?: colors["n1_100"] ?: 0xFFEFF4FA.toInt()
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

        // Extract genuine compositional layout variants from the seed
        val layoutIndex = (abs(config.seed) % 4).toInt()
        val colorVariant = ((abs(config.seed) / 4) % 3).toInt()

        when (config.preset) {
            WallpaperPreset.M3_EXPRESSIVE -> renderM3Expressive(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_MINIMAL -> renderPixelMinimal(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_BLOOM -> renderPixelBloom(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.SILK_DUNES -> renderSilkDunes(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_CLASSIC -> renderPixelClassic(canvas, w, h, resolved, paint, layoutIndex)
            WallpaperPreset.FLUID_WAVES -> renderFluidWaves(canvas, w, h, resolved, paint, layoutIndex)
            WallpaperPreset.BAUHAUS_MODERN -> renderBauhausModern(canvas, w, h, resolved, paint, layoutIndex)
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
            val r = baseRadius + amplitude * cos((lobes * theta).toDouble()).toFloat()
            val x = cx + r * cos(theta.toDouble()).toFloat()
            val y = cy + r * sin(theta.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawOrganicCloudLobe(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rx: Float,
        ry: Float,
        paint: Paint,
    ) {
        val rectF = RectF(cx - rx, cy - ry, cx + rx, cy + ry)
        canvas.drawOval(rectF, paint)
    }

    private fun drawSeaUrchin(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        baseRadius: Float,
        spikeAmplitude: Float,
        spikes: Int = 12,
        paint: Paint,
    ) {
        val path = Path()
        val stepCount = spikes * 12
        for (i in 0 until stepCount) {
            val theta = (i * 2.0 * Math.PI / stepCount).toFloat()
            val wave = cos((spikes * theta).toDouble()).toFloat()
            val harmonic = 0.25f * cos((2 * spikes * theta).toDouble()).toFloat()
            val r = baseRadius + spikeAmplitude * (wave + harmonic)
            val x = cx + r * cos(theta.toDouble()).toFloat()
            val y = cy + r * sin(theta.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawClover(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        leaves: Int = 4,
        paint: Paint,
    ) {
        val path = Path()
        val stepCount = leaves * 16
        for (i in 0 until stepCount) {
            val theta = (i * 2.0 * Math.PI / stepCount).toFloat()
            val r = radius * (0.75f + 0.25f * cos((leaves * theta).toDouble()).toFloat())
            val x = cx + r * cos(theta.toDouble()).toFloat()
            val y = cy + r * sin(theta.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    private fun drawSunny(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        rays: Int = 8,
        paint: Paint,
    ) {
        val path = Path()
        val stepCount = rays * 12
        for (i in 0 until stepCount) {
            val theta = (i * 2.0 * Math.PI / stepCount).toFloat()
            val r = radius * (0.84f + 0.16f * cos((rays * theta).toDouble()).toFloat())
            val x = cx + r * cos(theta.toDouble()).toFloat()
            val y = cy + r * sin(theta.toDouble()).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    // =========================================================================
    // 0. M3_EXPRESSIVE (Official Material 3 Expressive Shape Family)
    // Features Sea Urchin (海胆), 4/8-Leaf Clover (四叶草), Sunny (阳光太阳)
    // =========================================================================
    private fun renderM3Expressive(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        val urchinColor = when (colorVariant) {
            0 -> colors.vibrant
            1 -> colors.primary
            else -> colors.accent
        }
        val cloverColor = when (colorVariant) {
            0 -> colors.tertiary
            1 -> colors.secondary
            else -> colors.primary
        }
        val sunnyColor = when (colorVariant) {
            0 -> colors.yellowPop
            1 -> colors.accent
            else -> colors.softDune
        }
        val pillColor = colors.secondary

        when (layout) {
            0 -> {
                // Layout 0: Sea Urchin Hero at top-right, 4-leaf Clover at bottom-left, Sunny accent at mid-left
                paint.color = urchinColor
                drawSeaUrchin(canvas, 0.78f * w, 0.22f * h, 0.26f * w, 0.08f * w, 12, paint)

                paint.color = cloverColor
                drawClover(canvas, 0.30f * w, 0.82f * h, 0.28f * w, 4, paint)

                paint.color = sunnyColor
                drawSunny(canvas, 0.16f * w, 0.42f * h, 0.18f * w, 8, paint)

                paint.color = pillColor
                drawRotatedSquircle(canvas, 0.82f * w, 0.68f * h, 0.26f * w, 0.08f * w, 15f, paint)
            }
            1 -> {
                // Layout 1 (Mirrored): Sea Urchin at top-left, 4-leaf Clover at bottom-right, Sunny at mid-right
                paint.color = urchinColor
                drawSeaUrchin(canvas, 0.22f * w, 0.22f * h, 0.26f * w, 0.08f * w, 12, paint)

                paint.color = cloverColor
                drawClover(canvas, 0.70f * w, 0.82f * h, 0.28f * w, 4, paint)

                paint.color = sunnyColor
                drawSunny(canvas, 0.84f * w, 0.42f * h, 0.18f * w, 8, paint)

                paint.color = pillColor
                drawRotatedSquircle(canvas, 0.18f * w, 0.68f * h, 0.26f * w, 0.08f * w, -15f, paint)
            }
            2 -> {
                // Layout 2: Sunny Sunburst Hero at top-center, Sea Urchin at bottom-right, 8-leaf Clover at mid-left
                paint.color = sunnyColor
                drawSunny(canvas, 0.50f * w, 0.16f * h, 0.26f * w, 12, paint)

                paint.color = urchinColor
                drawSeaUrchin(canvas, 0.78f * w, 0.84f * h, 0.28f * w, 0.075f * w, 12, paint)

                paint.color = cloverColor
                drawClover(canvas, 0.18f * w, 0.58f * h, 0.22f * w, 8, paint)
            }
            else -> {
                // Layout 3: 4-leaf Clover Hero at top-right, Sea Urchin at bottom-left, Sunny at top-left
                paint.color = cloverColor
                drawClover(canvas, 0.75f * w, 0.20f * h, 0.28f * w, 4, paint)

                paint.color = urchinColor
                drawSeaUrchin(canvas, 0.25f * w, 0.85f * h, 0.26f * w, 0.075f * w, 12, paint)

                paint.color = sunnyColor
                drawSunny(canvas, 0.18f * w, 0.12f * h, 0.16f * w, 8, paint)

                paint.color = pillColor
                drawOrganicCloudLobe(canvas, 0.85f * w, 0.60f * h, 0.34f * w, 0.18f * h, paint)
            }
        }
    }

    // =========================================================================
    // 1. PIXEL_MINIMAL (Strict adherence to Reference 1)
    // Cohesive 45° Squircles + balancing soft organic lobe. Generous negative space.
    // =========================================================================
    private fun renderPixelMinimal(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        val c1 = when (colorVariant) {
            0 -> colors.primary
            1 -> colors.secondary
            else -> colors.tertiary
        }
        val c2 = when (colorVariant) {
            0 -> colors.tertiary
            1 -> colors.primary
            else -> colors.slate
        }
        val c3 = when (colorVariant) {
            0 -> colors.slate
            1 -> colors.tertiary
            else -> colors.primary
        }
        val cloudColor = colors.secondary

        when (layout) {
            0 -> {
                // Canonical Reference 1: Left-anchored diagonal squircles + right balancing lobe
                paint.color = cloudColor
                drawOrganicCloudLobe(canvas, 0.85f * w, 0.45f * h, 0.42f * w, 0.22f * h, paint)

                paint.color = c1
                drawRotatedSquircle(canvas, 0.15f * w, 0.10f * h, 0.44f * w, 0.14f * w, 45f, paint)

                paint.color = c2
                drawRotatedSquircle(canvas, -0.05f * w, 0.52f * h, 0.40f * w, 0.13f * w, 45f, paint)

                paint.color = c3
                drawRotatedSquircle(canvas, 0.35f * w, 0.90f * h, 0.62f * w, 0.20f * w, 45f, paint)
            }
            1 -> {
                // Mirrored: Right-anchored diagonal squircles + left balancing lobe
                paint.color = cloudColor
                drawOrganicCloudLobe(canvas, 0.15f * w, 0.45f * h, 0.42f * w, 0.22f * h, paint)

                paint.color = c1
                drawRotatedSquircle(canvas, 0.85f * w, 0.10f * h, 0.44f * w, 0.14f * w, 45f, paint)

                paint.color = c2
                drawRotatedSquircle(canvas, 1.05f * w, 0.52f * h, 0.40f * w, 0.13f * w, 45f, paint)

                paint.color = c3
                drawRotatedSquircle(canvas, 0.65f * w, 0.90f * h, 0.62f * w, 0.20f * w, 45f, paint)
            }
            2 -> {
                // Diagonal Cross-Balance: Top-right & Bottom-left anchors
                paint.color = cloudColor
                drawOrganicCloudLobe(canvas, 0.85f * w, 0.50f * h, 0.38f * w, 0.20f * h, paint)

                paint.color = c1
                drawRotatedSquircle(canvas, 0.80f * w, 0.14f * h, 0.42f * w, 0.13f * w, 45f, paint)

                paint.color = c2
                drawRotatedSquircle(canvas, 0.05f * w, 0.46f * h, 0.36f * w, 0.11f * w, 45f, paint)

                paint.color = c3
                drawRotatedSquircle(canvas, 0.70f * w, 0.86f * h, 0.54f * w, 0.17f * w, 45f, paint)
            }
            else -> {
                // Perimeter Spine: Anchored top & bottom with edge balance
                paint.color = cloudColor
                drawOrganicCloudLobe(canvas, 0.08f * w, 0.62f * h, 0.36f * w, 0.18f * h, paint)

                paint.color = c1
                drawRotatedSquircle(canvas, 0.35f * w, 0.08f * h, 0.42f * w, 0.13f * w, 45f, paint)

                paint.color = c2
                drawRotatedSquircle(canvas, 0.98f * w, 0.40f * h, 0.38f * w, 0.12f * w, 45f, paint)

                paint.color = c3
                drawRotatedSquircle(canvas, 0.65f * w, 0.92f * h, 0.58f * w, 0.18f * w, 45f, paint)
            }
        }
    }

    // =========================================================================
    // 2. PIXEL_BLOOM (Strict adherence to Reference 2)
    // 12-lobed Flower Badge + Arch Dome + Flowing Edge Wave
    // =========================================================================
    private fun renderPixelBloom(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        val flowerColor = when (colorVariant) {
            0 -> colors.vibrant
            1 -> colors.primary
            else -> colors.tertiary
        }
        val archColor = when (colorVariant) {
            0 -> colors.primary
            1 -> colors.secondary
            else -> colors.slate
        }
        val waveColor = colors.secondary

        when (layout) {
            0 -> {
                // Canonical Reference 2: Top-right dome, left vertical wave, bottom-right flower badge
                paint.color = archColor
                canvas.drawOval(RectF(0.35f * w, -0.12f * h, 1.35f * w, 0.32f * h), paint)

                paint.color = waveColor
                canvas.drawOval(RectF(-0.30f * w, 0.20f * h, 0.32f * w, 0.78f * h), paint)

                paint.color = flowerColor
                drawScallopedBadge(canvas, 0.82f * w, 0.85f * h, 0.34f * w, 0.028f * w, 12, paint)
            }
            1 -> {
                // Mirrored: Top-left dome, right vertical wave, bottom-left flower badge
                paint.color = archColor
                canvas.drawOval(RectF(-0.35f * w, -0.12f * h, 0.65f * w, 0.32f * h), paint)

                paint.color = waveColor
                canvas.drawOval(RectF(0.68f * w, 0.20f * h, 1.30f * w, 0.78f * h), paint)

                paint.color = flowerColor
                drawScallopedBadge(canvas, 0.18f * w, 0.85f * h, 0.34f * w, 0.028f * w, 12, paint)
            }
            2 -> {
                // Top-anchored Flower Badge with bottom dome
                paint.color = archColor
                canvas.drawOval(RectF(-0.15f * w, 0.75f * h, 0.65f * w, 1.15f * h), paint)

                paint.color = waveColor
                canvas.drawOval(RectF(0.70f * w, 0.40f * h, 1.25f * w, 0.88f * h), paint)

                paint.color = flowerColor
                drawScallopedBadge(canvas, 0.80f * w, 0.18f * h, 0.30f * w, 0.025f * w, 12, paint)
            }
            else -> {
                // Center-Offset Bottom Badge with top wide dome
                paint.color = archColor
                canvas.drawOval(RectF(0.10f * w, -0.15f * h, 0.90f * w, 0.25f * h), paint)

                paint.color = waveColor
                canvas.drawOval(RectF(-0.25f * w, 0.35f * h, 0.28f * w, 0.75f * h), paint)

                paint.color = flowerColor
                drawScallopedBadge(canvas, 0.78f * w, 0.82f * h, 0.32f * w, 0.026f * w, 12, paint)
            }
        }
    }

    // =========================================================================
    // 3. SILK_DUNES (Strict adherence to Reference 3)
    // Continuous sweeping diagonal ribbon strata with coordinated cubic beziers.
    // =========================================================================
    private fun renderSilkDunes(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        val ribbonPrimary = when (colorVariant) {
            0 -> colors.primary
            1 -> colors.softDune
            else -> colors.tertiary
        }
        val ribbonSecondary = when (colorVariant) {
            0 -> colors.softDune
            1 -> colors.primary
            else -> colors.secondary
        }
        val crestColor = colors.lightDune
        val popColor = colors.yellowPop

        when (layout) {
            0 -> {
                // Canonical Reference 3: Sweeping from top-right to bottom-left
                paint.color = crestColor
                val pathCrest = Path().apply {
                    moveTo(0.50f * w, 0f)
                    cubicTo(0.70f * w, 0.08f * h, 0.85f * w, 0.16f * h, w, 0.28f * h)
                    lineTo(w, 0f)
                    close()
                }
                canvas.drawPath(pathCrest, paint)

                paint.color = ribbonPrimary
                val pathCoral = Path().apply {
                    moveTo(0f, 0.06f * h)
                    cubicTo(0.35f * w, 0.16f * h, 0.70f * w, 0.38f * h, w, 0.58f * h)
                    lineTo(w, 0.72f * h)
                    cubicTo(0.65f * w, 0.48f * h, 0.30f * w, 0.28f * h, 0f, 0.22f * h)
                    close()
                }
                canvas.drawPath(pathCoral, paint)

                paint.color = ribbonSecondary
                val pathApricot = Path().apply {
                    moveTo(0f, 0.36f * h)
                    cubicTo(0.35f * w, 0.48f * h, 0.65f * w, 0.68f * h, w, 0.88f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathApricot, paint)

                paint.color = popColor
                val pathPop = Path().apply {
                    moveTo(0f, 0.75f * h)
                    cubicTo(0.12f * w, 0.82f * h, 0.18f * w, 0.90f * h, 0.08f * w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathPop, paint)
            }
            1 -> {
                // Mirrored: Sweeping from top-left to bottom-right
                paint.color = crestColor
                val pathCrest = Path().apply {
                    moveTo(0.50f * w, 0f)
                    cubicTo(0.30f * w, 0.08f * h, 0.15f * w, 0.16f * h, 0f, 0.28f * h)
                    lineTo(0f, 0f)
                    close()
                }
                canvas.drawPath(pathCrest, paint)

                paint.color = ribbonPrimary
                val pathCoral = Path().apply {
                    moveTo(w, 0.06f * h)
                    cubicTo(0.65f * w, 0.16f * h, 0.30f * w, 0.38f * h, 0f, 0.58f * h)
                    lineTo(0f, 0.72f * h)
                    cubicTo(0.35f * w, 0.48f * h, 0.70f * w, 0.28f * h, w, 0.22f * h)
                    close()
                }
                canvas.drawPath(pathCoral, paint)

                paint.color = ribbonSecondary
                val pathApricot = Path().apply {
                    moveTo(w, 0.36f * h)
                    cubicTo(0.65f * w, 0.48f * h, 0.35f * w, 0.68f * h, 0f, 0.88f * h)
                    lineTo(0f, h)
                    lineTo(w, h)
                    close()
                }
                canvas.drawPath(pathApricot, paint)

                paint.color = popColor
                val pathPop = Path().apply {
                    moveTo(w, 0.75f * h)
                    cubicTo(0.88f * w, 0.82f * h, 0.82f * w, 0.90f * h, 0.92f * w, h)
                    lineTo(w, h)
                    close()
                }
                canvas.drawPath(pathPop, paint)
            }
            2 -> {
                // Gentle S-curve horizontal undulating strata
                paint.color = crestColor
                val pathCrest = Path().apply {
                    moveTo(0f, 0.14f * h)
                    cubicTo(0.35f * w, 0.10f * h, 0.65f * w, 0.18f * h, w, 0.12f * h)
                    lineTo(w, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                canvas.drawPath(pathCrest, paint)

                paint.color = ribbonPrimary
                val pathBand = Path().apply {
                    moveTo(0f, 0.26f * h)
                    cubicTo(0.30f * w, 0.32f * h, 0.70f * w, 0.24f * h, w, 0.30f * h)
                    lineTo(w, 0.48f * h)
                    cubicTo(0.70f * w, 0.42f * h, 0.30f * w, 0.50f * h, 0f, 0.44f * h)
                    close()
                }
                canvas.drawPath(pathBand, paint)

                paint.color = ribbonSecondary
                val pathLower = Path().apply {
                    moveTo(0f, 0.58f * h)
                    cubicTo(0.35f * w, 0.64f * h, 0.65f * w, 0.56f * h, w, 0.62f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathLower, paint)

                paint.color = popColor
                val pathPop = Path().apply {
                    moveTo(0.35f * w, h)
                    cubicTo(0.50f * w, 0.88f * h, 0.65f * w, 0.92f * h, 0.75f * w, h)
                    close()
                }
                canvas.drawPath(pathPop, paint)
            }
            else -> {
                // Steep diagonal strata (high aesthetic tension)
                paint.color = crestColor
                val pathCrest = Path().apply {
                    moveTo(0.20f * w, 0f)
                    lineTo(w, 0f)
                    lineTo(w, 0.38f * h)
                    close()
                }
                canvas.drawPath(pathCrest, paint)

                paint.color = ribbonPrimary
                val pathBand = Path().apply {
                    moveTo(0f, 0.15f * h)
                    lineTo(w, 0.65f * h)
                    lineTo(w, 0.78f * h)
                    lineTo(0f, 0.32f * h)
                    close()
                }
                canvas.drawPath(pathBand, paint)

                paint.color = ribbonSecondary
                val pathLower = Path().apply {
                    moveTo(0f, 0.48f * h)
                    lineTo(w, 0.95f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathLower, paint)

                paint.color = popColor
                val pathPop = Path().apply {
                    moveTo(0f, 0.82f * h)
                    lineTo(0.22f * w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathPop, paint)
            }
        }
    }

    // =========================================================================
    // 4. PIXEL_CLASSIC (Scaled-down compact with 4 genuine layout variants)
    // =========================================================================
    private fun renderPixelClassic(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
    ) {
        when (layout) {
            0 -> {
                // Standard Classic
                paint.color = colors.circle
                canvas.drawCircle(0.50f * w, 0.40f * h, 0.115f * w, paint)

                paint.color = colors.dot
                canvas.drawCircle(0.68f * w, 0.33f * h, 0.032f * w, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.85f * w, 0.54f * h, 0.28f * w, 0.08f * w, -4f, paint)

                paint.color = colors.wave
                val path = Path().apply {
                    moveTo(0f, 0.48f * h)
                    cubicTo(0.12f * w, 0.52f * h, 0.36f * w, 0.56f * h, 0.38f * w, 0.66f * h)
                    cubicTo(0.38f * w, 0.74f * h, 0.46f * w, 0.80f * h, 0.45f * w, 0.88f * h)
                    cubicTo(0.44f * w, 0.94f * h, 0.35f * w, 0.98f * h, 0.28f * w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(path, paint)
            }
            1 -> {
                // Mirrored Classic
                paint.color = colors.circle
                canvas.drawCircle(0.50f * w, 0.40f * h, 0.115f * w, paint)

                paint.color = colors.dot
                canvas.drawCircle(0.32f * w, 0.33f * h, 0.032f * w, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.15f * w, 0.54f * h, 0.28f * w, 0.08f * w, 4f, paint)

                paint.color = colors.wave
                val path = Path().apply {
                    moveTo(w, 0.48f * h)
                    cubicTo(0.88f * w, 0.52f * h, 0.64f * w, 0.56f * h, 0.62f * w, 0.66f * h)
                    cubicTo(0.62f * w, 0.74f * h, 0.54f * w, 0.80f * h, 0.55f * w, 0.88f * h)
                    cubicTo(0.56f * w, 0.94f * h, 0.65f * w, 0.98f * h, 0.72f * w, h)
                    lineTo(w, h)
                    close()
                }
                canvas.drawPath(path, paint)
            }
            2 -> {
                // Diagonal Classic (Top-anchored wave)
                paint.color = colors.wave
                val path = Path().apply {
                    moveTo(0f, 0.25f * h)
                    cubicTo(0.20f * w, 0.18f * h, 0.45f * w, 0.22f * h, 0.55f * w, 0.10f * h)
                    lineTo(0.55f * w, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                canvas.drawPath(path, paint)

                paint.color = colors.circle
                canvas.drawCircle(0.48f * w, 0.58f * h, 0.12f * w, paint)

                paint.color = colors.dot
                canvas.drawCircle(0.28f * w, 0.45f * h, 0.035f * w, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.80f * w, 0.75f * h, 0.30f * w, 0.09f * w, -8f, paint)
            }
            else -> {
                // Center-Spine Classic
                paint.color = colors.circle
                canvas.drawCircle(0.50f * w, 0.28f * h, 0.12f * w, paint)

                paint.color = colors.dot
                canvas.drawCircle(0.68f * w, 0.22f * h, 0.032f * w, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.25f * w, 0.45f * h, 0.26f * w, 0.08f * w, 6f, paint)

                paint.color = colors.wave
                val path = Path().apply {
                    moveTo(0f, 0.72f * h)
                    cubicTo(0.30f * w, 0.68f * h, 0.70f * w, 0.78f * h, w, 0.70f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(path, paint)
            }
        }
    }

    // =========================================================================
    // 5. FLUID_WAVES (Scaled-down with genuine layout variants)
    // =========================================================================
    private fun renderFluidWaves(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
    ) {
        when (layout) {
            0 -> {
                paint.color = colors.secondaryWave
                val pathSec = Path().apply {
                    moveTo(w, 0.18f * h)
                    cubicTo(0.78f * w, 0.22f * h, 0.58f * w, 0.14f * h, 0.46f * w, 0.28f * h)
                    cubicTo(0.35f * w, 0.40f * h, 0.60f * w, 0.50f * h, 0.85f * w, 0.54f * h)
                    lineTo(w, 0.54f * h)
                    close()
                }
                canvas.drawPath(pathSec, paint)

                paint.color = colors.circle
                canvas.drawCircle(0.32f * w, 0.38f * h, 0.125f * w, paint)

                paint.color = colors.wave
                val pathMain = Path().apply {
                    moveTo(0f, 0.55f * h)
                    cubicTo(0.22f * w, 0.58f * h, 0.40f * w, 0.72f * h, 0.32f * w, 0.82f * h)
                    cubicTo(0.26f * w, 0.90f * h, 0.48f * w, 0.96f * h, 0.58f * w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathMain, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.82f * w, 0.75f * h, 0.24f * w, 0.08f * w, -12f, paint)
            }
            1 -> {
                // Mirrored
                paint.color = colors.secondaryWave
                val pathSec = Path().apply {
                    moveTo(0f, 0.18f * h)
                    cubicTo(0.22f * w, 0.22f * h, 0.42f * w, 0.14f * h, 0.54f * w, 0.28f * h)
                    cubicTo(0.65f * w, 0.40f * h, 0.40f * w, 0.50f * h, 0.15f * w, 0.54f * h)
                    lineTo(0f, 0.54f * h)
                    close()
                }
                canvas.drawPath(pathSec, paint)

                paint.color = colors.circle
                canvas.drawCircle(0.68f * w, 0.38f * h, 0.125f * w, paint)

                paint.color = colors.wave
                val pathMain = Path().apply {
                    moveTo(w, 0.55f * h)
                    cubicTo(0.78f * w, 0.58f * h, 0.60f * w, 0.72f * h, 0.68f * w, 0.82f * h)
                    cubicTo(0.74f * w, 0.90f * h, 0.52f * w, 0.96f * h, 0.42f * w, h)
                    lineTo(w, h)
                    close()
                }
                canvas.drawPath(pathMain, paint)

                paint.color = colors.rect
                drawRotatedSquircle(canvas, 0.18f * w, 0.75f * h, 0.24f * w, 0.08f * w, 12f, paint)
            }
            else -> {
                // Cascading Waves
                paint.color = colors.secondaryWave
                val pathTop = Path().apply {
                    moveTo(0f, 0.20f * h)
                    cubicTo(0.35f * w, 0.28f * h, 0.65f * w, 0.16f * h, w, 0.24f * h)
                    lineTo(w, 0f)
                    lineTo(0f, 0f)
                    close()
                }
                canvas.drawPath(pathTop, paint)

                paint.color = colors.circle
                canvas.drawCircle(0.50f * w, 0.48f * h, 0.13f * w, paint)

                paint.color = colors.wave
                val pathBottom = Path().apply {
                    moveTo(0f, 0.70f * h)
                    cubicTo(0.40f * w, 0.62f * h, 0.60f * w, 0.78f * h, w, 0.68f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathBottom, paint)
            }
        }
    }

    // =========================================================================
    // 6. BAUHAUS_MODERN (Slender modern architectural proportions)
    // =========================================================================
    private fun renderBauhausModern(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
    ) {
        when (layout) {
            0 -> {
                paint.color = colors.secondaryWave
                canvas.drawCircle(0.72f * w, 0.32f * h, 0.20f * w, paint)

                paint.color = colors.rect
                val px = 0.32f * w
                val py = 0.48f * h
                val pw = 0.20f * w
                val ph = 0.34f * h
                canvas.save()
                canvas.rotate(8f, px, py)
                canvas.drawRoundRect(RectF(px - pw / 2f, py - ph / 2f, px + pw / 2f, py + ph / 2f), 0.10f * w, 0.10f * w, paint)
                canvas.restore()

                paint.color = colors.circle
                canvas.drawCircle(0.52f * w, 0.58f * h, 0.13f * w, paint)

                paint.color = colors.wave
                val pathBase = Path().apply {
                    moveTo(0f, 0.90f * h)
                    cubicTo(0.30f * w, 0.86f * h, 0.70f * w, 0.94f * h, w, 0.90f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathBase, paint)
            }
            1 -> {
                // Mirrored
                paint.color = colors.secondaryWave
                canvas.drawCircle(0.28f * w, 0.32f * h, 0.20f * w, paint)

                paint.color = colors.rect
                val px = 0.68f * w
                val py = 0.48f * h
                val pw = 0.20f * w
                val ph = 0.34f * h
                canvas.save()
                canvas.rotate(-8f, px, py)
                canvas.drawRoundRect(RectF(px - pw / 2f, py - ph / 2f, px + pw / 2f, py + ph / 2f), 0.10f * w, 0.10f * w, paint)
                canvas.restore()

                paint.color = colors.circle
                canvas.drawCircle(0.48f * w, 0.58f * h, 0.13f * w, paint)

                paint.color = colors.wave
                val pathBase = Path().apply {
                    moveTo(0f, 0.90f * h)
                    cubicTo(0.30f * w, 0.94f * h, 0.70f * w, 0.86f * h, w, 0.90f * h)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                canvas.drawPath(pathBase, paint)
            }
            else -> {
                // Dual Column
                paint.color = colors.secondaryWave
                canvas.drawCircle(0.50f * w, 0.28f * h, 0.18f * w, paint)

                paint.color = colors.rect
                canvas.drawRoundRect(RectF(0.20f * w, 0.42f * h, 0.40f * w, 0.78f * h), 0.10f * w, 0.10f * w, paint)

                paint.color = colors.circle
                canvas.drawRoundRect(RectF(0.60f * w, 0.50f * h, 0.80f * w, 0.86f * h), 0.10f * w, 0.10f * w, paint)

                paint.color = colors.wave
                canvas.drawRect(0f, 0.92f * h, w, h, paint)
            }
        }
    }
}
