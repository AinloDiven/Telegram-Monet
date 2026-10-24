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
import kotlin.math.min
import kotlin.math.sin

data class ResolvedWallpaperColors(
    val bgSoft: Int,
    val bgToned: Int,
    val primarySoft: Int,
    val primaryPastel: Int,
    val primaryBold: Int,
    val secondarySoft: Int,
    val secondaryPastel: Int,
    val secondaryMid: Int,
    val tertiarySoft: Int,
    val tertiaryPastel: Int,
    val tertiaryMid: Int,
    val neutralMid: Int,
    val accentPop: Int,
)

object PixelWallpaperGenerator {

    fun resolveColors(
        colors: Map<String, Int>,
        isDark: Boolean,
        isAmoled: Boolean,
    ): ResolvedWallpaperColors {
        return if (!isDark) {
            val bgSoft = colors["n1_50"] ?: colors["n1_100"] ?: 0xFFEEF3FC.toInt()
            val bgToned = colors["a1_100"] ?: colors["a1_200"] ?: 0xFFFEA49C.toInt()
            val primarySoft = colors["a1_200"] ?: 0xFFB8D4FF.toInt()
            val primaryPastel = colors["a1_300"] ?: 0xFF92BFFF.toInt()
            val primaryBold = colors["a1_500"] ?: colors["a1_400"] ?: 0xFF4482F6.toInt()
            val secondarySoft = colors["a2_100"] ?: 0xFFD5E3FA.toInt()
            val secondaryPastel = colors["a2_200"] ?: 0xFFC2D7FF.toInt()
            val secondaryMid = colors["a2_300"] ?: 0xFFA6C2ED.toInt()
            val tertiarySoft = colors["a3_100"] ?: 0xFFFFD9CC.toInt()
            val tertiaryPastel = colors["a3_200"] ?: 0xFFFAF473.toInt()
            val tertiaryMid = colors["a3_300"] ?: 0xFF6CB49F.toInt()
            val neutralMid = colors["n2_200"] ?: colors["n2_300"] ?: 0xFFB7C5D8.toInt()
            val accentPop = colors["a3_500"] ?: colors["a1_600"] ?: 0xFFF42D20.toInt()
            ResolvedWallpaperColors(
                bgSoft = bgSoft,
                bgToned = bgToned,
                primarySoft = primarySoft,
                primaryPastel = primaryPastel,
                primaryBold = primaryBold,
                secondarySoft = secondarySoft,
                secondaryPastel = secondaryPastel,
                secondaryMid = secondaryMid,
                tertiarySoft = tertiarySoft,
                tertiaryPastel = tertiaryPastel,
                tertiaryMid = tertiaryMid,
                neutralMid = neutralMid,
                accentPop = accentPop,
            )
        } else {
            val bgSoft = if (isAmoled) {
                colors["n1_1000"] ?: 0xFF000000.toInt()
            } else {
                colors["n1_900"] ?: 0xFF141218.toInt()
            }
            val bgToned = if (isAmoled) {
                colors["n1_1000"] ?: 0xFF000000.toInt()
            } else {
                colors["n2_900"] ?: colors["n1_900"] ?: 0xFF1C1922.toInt()
            }
            val primarySoft = colors["a1_800"] ?: 0xFF233654.toInt()
            val primaryPastel = colors["a1_700"] ?: 0xFF2E476E.toInt()
            val primaryBold = colors["a1_600"] ?: 0xFF3B5B8C.toInt()
            val secondarySoft = colors["a2_800"] ?: 0xFF283140.toInt()
            val secondaryPastel = colors["a2_700"] ?: 0xFF354154.toInt()
            val secondaryMid = colors["a2_600"] ?: 0xFF46556D.toInt()
            val tertiarySoft = colors["a3_800"] ?: 0xFF223D36.toInt()
            val tertiaryPastel = colors["a3_600"] ?: 0xFF386459.toInt()
            val tertiaryMid = colors["a3_700"] ?: 0xFF2C5047.toInt()
            val neutralMid = colors["n2_700"] ?: 0xFF3A3F4B.toInt()
            val accentPop = colors["a3_400"] ?: colors["a1_400"] ?: 0xFFFF897D.toInt()
            ResolvedWallpaperColors(
                bgSoft = bgSoft,
                bgToned = bgToned,
                primarySoft = primarySoft,
                primaryPastel = primaryPastel,
                primaryBold = primaryBold,
                secondarySoft = secondarySoft,
                secondaryPastel = secondaryPastel,
                secondaryMid = secondaryMid,
                tertiarySoft = tertiarySoft,
                tertiaryPastel = tertiaryPastel,
                tertiaryMid = tertiaryMid,
                neutralMid = neutralMid,
                accentPop = accentPop,
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
            isDither = true
        }

        val layoutIndex = (abs(config.seed) % 4).toInt()
        val colorVariant = ((abs(config.seed) / 4) % 3).toInt()

        when (config.preset) {
            WallpaperPreset.M3_EXPRESSIVE -> renderM3Expressive(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_MINIMAL -> renderPixelMinimal(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_BLOOM -> renderPixelBloom(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.SILK_DUNES -> renderSilkDunes(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.PIXEL_CLASSIC -> renderPixelClassic(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.FLUID_WAVES -> renderFluidWaves(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
            WallpaperPreset.BAUHAUS_MODERN -> renderBauhausModern(canvas, w, h, resolved, paint, layoutIndex, colorVariant)
        }
    }

    // =========================================================================
    // Core Geometric Primitives (Exact C2 Cubic Bezier Star / Cookie / Squircle)
    // =========================================================================

    private inline fun withQuadrantTransform(
        canvas: Canvas,
        w: Float,
        h: Float,
        layout: Int,
        block: () -> Unit,
    ) {
        val sx = if (layout == 1 || layout == 3) -1f else 1f
        val sy = if (layout == 2 || layout == 3) -1f else 1f
        canvas.save()
        canvas.scale(sx, sy, w / 2f, h / 2f)
        block()
        canvas.restore()
    }

    /**
     * Draws a tilted rounded rectangle / squircle / stadium pill around (cx, cy).
     */
    private fun drawTiltedRoundRect(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        rw: Float,
        rh: Float,
        cornerRadius: Float,
        angleDeg: Float,
        paint: Paint,
    ) {
        val cr = min(cornerRadius, min(rw, rh) / 2f)
        canvas.save()
        canvas.rotate(angleDeg, cx, cy)
        val rectF = RectF(cx - rw / 2f, cy - rh / 2f, cx + rw / 2f, cy + rh / 2f)
        canvas.drawRoundRect(rectF, cr, cr, paint)
        canvas.restore()
    }

    /**
     * Draws a smooth C2-continuous Material 3 Expressive Star / Cookie / Clover / Burst polygon
     * using exact circumferential cubic Bezier tangents at alternating peaks and valleys.
     *
     * Key shape recipes:
     * - Cookie4Sided (Sample 2 Cloud): lobes=4, innerRatio=0.72f, peakRound=0.37f, valleyRound=0.37f
     * - Clover4Leaf (4-Leaf Clover):   lobes=4, innerRatio=0.46f, peakRound=0.58f, valleyRound=0.08f
     * - Clover8Leaf (8-Leaf Flower):   lobes=8, innerRatio=0.70f, peakRound=0.48f, valleyRound=0.12f
     * - Sunny (8-Ray Sunburst):        lobes=8, innerRatio=0.80f, peakRound=0.30f, valleyRound=0.35f
     * - SoftBurst / Sea Urchin:        lobes=16, innerRatio=0.80f, peakRound=0.26f, valleyRound=0.30f
     * - Cookie9Sided (Sample 3 Bloom): lobes=9, innerRatio=0.90f, peakRound=0.363f, valleyRound=0.363f
     */
    private fun drawRoundedStarPolygon(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        outerRadius: Float,
        innerRatio: Float,
        lobes: Int,
        peakRound: Float,
        valleyRound: Float,
        rotDeg: Float = 0f,
        paint: Paint,
    ) {
        val innerRadius = outerRadius * innerRatio
        val rotRad = Math.toRadians(rotDeg.toDouble()).toFloat()
        val halfStep = (Math.PI / lobes).toFloat()
        val hOut = peakRound * outerRadius * halfStep
        val hIn = valleyRound * innerRadius * halfStep

        val path = Path()
        for (i in 0 until lobes) {
            val a0 = rotRad + i * 2f * halfStep
            val b0 = a0 + halfStep
            val a1 = a0 + 2f * halfStep

            val cosA0 = cos(a0.toDouble()).toFloat()
            val sinA0 = sin(a0.toDouble()).toFloat()
            val p0x = cx + outerRadius * cosA0
            val p0y = cy + outerRadius * sinA0
            val t0x = -sinA0
            val t0y = cosA0

            val cosB0 = cos(b0.toDouble()).toFloat()
            val sinB0 = sin(b0.toDouble()).toFloat()
            val v0x = cx + innerRadius * cosB0
            val v0y = cy + innerRadius * sinB0
            val u0x = -sinB0
            val u0y = cosB0

            val cosA1 = cos(a1.toDouble()).toFloat()
            val sinA1 = sin(a1.toDouble()).toFloat()
            val p1x = cx + outerRadius * cosA1
            val p1y = cy + outerRadius * sinA1
            val t1x = -sinA1
            val t1y = cosA1

            if (i == 0) {
                path.moveTo(p0x, p0y)
            }

            // Peak p0 -> Valley v0
            path.cubicTo(
                p0x + hOut * t0x,
                p0y + hOut * t0y,
                v0x - hIn * u0x,
                v0y - hIn * u0y,
                v0x,
                v0y,
            )

            // Valley v0 -> Next Peak p1
            path.cubicTo(
                v0x + hIn * u0x,
                v0y + hIn * u0y,
                p1x - hOut * t1x,
                p1y - hOut * t1y,
                p1x,
                p1y,
            )
        }
        path.close()
        canvas.drawPath(path, paint)
    }

    // =========================================================================
    // 0. M3_EXPRESSIVE (Official Material 3 Expressive Macro Shape Family)
    // Showcases Sea Urchin (16-ray SoftBurst), Clover4Leaf, Clover8Leaf, Sunny,
    // Cookie4Sided, and Stadium Pill in authentic edge-cropped Pixel layouts.
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
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val heroA = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.primaryBold
            else -> colors.tertiaryMid
        }
        val heroB = when (colorVariant) {
            0 -> colors.secondarySoft
            1 -> colors.primaryPastel
            else -> colors.secondaryPastel
        }
        val anchorC = when (colorVariant) {
            0 -> colors.tertiaryMid
            1 -> colors.secondaryMid
            else -> colors.primaryBold
        }
        val midCircle = colors.neutralMid
        val dotColor = colors.accentPop

        when (layout) {
            0 -> {
                // Layout 0: 16-Ray Sea Urchin (top-right) + 4-Leaf Clover (mid-left) + Tilted Pill (bottom-right) + Dot
                paint.color = heroA
                drawRoundedStarPolygon(canvas, 0.82f * w, 0.10f * h, 0.44f * w, 0.80f, 16, 0.26f, 0.30f, 11.25f, paint)

                paint.color = heroB
                drawRoundedStarPolygon(canvas, -0.04f * w, 0.50f * h, 0.44f * w, 0.46f, 4, 0.58f, 0.08f, 28f, paint)

                paint.color = anchorC
                drawTiltedRoundRect(canvas, 0.74f * w, 0.88f * h, 0.66f * w, 0.38f * w, 0.19f * w, -36f, paint)

                paint.color = dotColor
                canvas.drawCircle(0.30f * w, 0.23f * h, 0.048f * w, paint)
            }
            1 -> {
                // Layout 1: 8-Ray Sunny (top-left) + Cookie4Sided Cloud (mid-right) + 8-Leaf Flower (bottom-left) + Dot
                paint.color = heroA
                drawRoundedStarPolygon(canvas, 0.16f * w, 0.10f * h, 0.44f * w, 0.80f, 8, 0.30f, 0.35f, 22.5f, paint)

                paint.color = heroB
                drawRoundedStarPolygon(canvas, 0.92f * w, 0.46f * h, 0.54f * w, 0.72f, 4, 0.37f, 0.37f, 14f, paint)

                paint.color = anchorC
                drawRoundedStarPolygon(canvas, 0.22f * w, 0.86f * h, 0.40f * w, 0.70f, 8, 0.48f, 0.12f, 15f, paint)

                paint.color = dotColor
                canvas.drawCircle(0.68f * w, 0.74f * h, 0.048f * w, paint)
            }
            2 -> {
                // Layout 2: Giant Circle Dome (top-left) + Tilted Card (mid-right) + 16-Ray Sea Urchin (bottom-left) + Circle & Dot
                paint.color = heroB
                canvas.drawCircle(0.18f * w, 0.06f * h, 0.46f * w, paint)

                paint.color = anchorC
                drawTiltedRoundRect(canvas, 0.92f * w, 0.52f * h, 0.44f * w, 0.34f * h, 0.075f * w, 13f, paint)

                paint.color = colors.primaryBold
                drawRoundedStarPolygon(canvas, 0.22f * w, 0.86f * h, 0.42f * w, 0.80f, 16, 0.26f, 0.30f, 11.25f, paint)

                paint.color = heroA
                canvas.drawCircle(0.46f * w, 0.42f * h, 0.145f * w, paint)

                paint.color = dotColor
                canvas.drawCircle(0.66f * w, 0.33f * h, 0.048f * w, paint)
            }
            else -> {
                // Layout 3: 4-Leaf Clover (top-right) + Tilted Squircle (mid-left) + 8-Ray Sunny (bottom-right) + Circle & Dot
                paint.color = heroA
                drawRoundedStarPolygon(canvas, 0.84f * w, 0.11f * h, 0.44f * w, 0.46f, 4, 0.58f, 0.08f, 32f, paint)

                paint.color = anchorC
                drawTiltedRoundRect(canvas, -0.08f * w, 0.52f * h, 0.48f * w, 0.48f * w, 0.13f * w, 42f, paint)

                paint.color = colors.primaryBold
                drawRoundedStarPolygon(canvas, 0.78f * w, 0.85f * h, 0.41f * w, 0.80f, 8, 0.30f, 0.35f, 22.5f, paint)

                paint.color = midCircle
                canvas.drawCircle(0.46f * w, 0.45f * h, 0.14f * w, paint)

                paint.color = dotColor
                canvas.drawCircle(0.28f * w, 0.36f * h, 0.046f * w, paint)
            }
        }
    }

    // =========================================================================
    // 1. PIXEL_MINIMAL (Official Sample 2: Floating Squircles & 4-Sided Cookie)
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
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val topSqColor = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.tertiaryMid
            else -> colors.secondaryMid
        }
        val midSqColor = when (colorVariant) {
            0 -> colors.tertiaryMid
            1 -> colors.primaryPastel
            else -> colors.primaryBold
        }
        val botSqColor = when (colorVariant) {
            0 -> colors.neutralMid
            1 -> colors.secondaryPastel
            else -> colors.tertiaryMid
        }
        val cookieColor = colors.secondarySoft

        withQuadrantTransform(canvas, w, h, layout) {
            // Right edge-cropped 4-Sided Cookie Cloud
            paint.color = cookieColor
            drawRoundedStarPolygon(
                canvas = canvas,
                cx = 0.90f * w,
                cy = 0.452f * h,
                outerRadius = 0.58f * w,
                innerRatio = 0.72f,
                lobes = 4,
                peakRound = 0.37f,
                valleyRound = 0.37f,
                rotDeg = 13f,
                paint = paint,
            )

            // Top-left tilted squircle
            paint.color = topSqColor
            drawTiltedRoundRect(canvas, 0.14f * w, 0.11f * h, 0.54f * w, 0.54f * w, 0.14f * w, -38f, paint)

            // Mid-left tilted squircle
            paint.color = midSqColor
            drawTiltedRoundRect(canvas, -0.10f * w, 0.555f * h, 0.48f * w, 0.48f * w, 0.12f * w, 45f, paint)

            // Bottom tilted squircle
            paint.color = botSqColor
            drawTiltedRoundRect(canvas, 0.42f * w, 0.865f * h, 0.64f * w, 0.64f * w, 0.18f * w, -38f, paint)
        }
    }

    // =========================================================================
    // 2. PIXEL_BLOOM (Official Sample 3: Giant Dome, Multi-Lobe Cloud & Scallop)
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
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val domeColor = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.secondaryMid
            else -> colors.tertiaryMid
        }
        val cloudColor = when (colorVariant) {
            0 -> colors.secondaryPastel
            1 -> colors.secondarySoft
            else -> colors.primarySoft
        }
        val scallopColor = when (colorVariant) {
            0 -> colors.primaryBold
            1 -> colors.primaryPastel
            else -> colors.primaryBold
        }

        withQuadrantTransform(canvas, w, h, layout) {
            // 1. Top-right giant circle dome
            paint.color = domeColor
            canvas.drawCircle(0.81f * w, 0.075f * h, 0.50f * w, paint)

            // 2. Mid-left multi-lobe organic cloud (4-span cubic Bezier contour)
            paint.color = cloudColor
            val cloudPath = Path().apply {
                moveTo(0f, 0.195f * h)
                cubicTo(0.105f * w, 0.205f * h, 0.145f * w, 0.275f * h, 0.178f * w, 0.355f * h)
                cubicTo(0.21f * w, 0.43f * h, 0.382f * w, 0.445f * h, 0.382f * w, 0.535f * h)
                cubicTo(0.382f * w, 0.625f * h, 0.22f * w, 0.655f * h, 0.142f * w, 0.715f * h)
                cubicTo(0.08f * w, 0.765f * h, 0.045f * w, 0.795f * h, 0f, 0.802f * h)
                close()
            }
            canvas.drawPath(cloudPath, paint)

            // 3. Bottom-right 9-lobed soft scallop cookie
            paint.color = scallopColor
            drawRoundedStarPolygon(
                canvas = canvas,
                cx = 0.765f * w,
                cy = 0.848f * h,
                outerRadius = 0.425f * w,
                innerRatio = 0.905f,
                lobes = 9,
                peakRound = 0.363f,
                valleyRound = 0.363f,
                rotDeg = 16f,
                paint = paint,
            )
        }
    }

    // =========================================================================
    // 3. SILK_DUNES (Official Sample 4: Diagonal Strata & Nested Ribbon)
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
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val upperMassColor = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.tertiaryMid
            else -> colors.secondaryMid
        }
        val ribbonColor = colors.secondarySoft
        val lowerWaveColor = when (colorVariant) {
            0 -> colors.tertiarySoft
            1 -> colors.primarySoft
            else -> colors.secondaryPastel
        }
        val cornerPopColor = colors.tertiaryPastel

        withQuadrantTransform(canvas, w, h, layout) {
            // 1. Upper-right sweeping S-curve mass
            paint.color = upperMassColor
            val pathUpper = Path().apply {
                moveTo(0f, 0.032f * h)
                cubicTo(0.24f * w, 0.08f * h, 0.35f * w, 0.19f * h, 0.44f * w, 0.30f * h)
                cubicTo(0.54f * w, 0.42f * h, 0.74f * w, 0.47f * h, w, 0.568f * h)
                lineTo(w, 0f)
                lineTo(0f, 0f)
                close()
            }
            canvas.drawPath(pathUpper, paint)

            // 2. Top-right nested S-curve ribbon band
            paint.color = ribbonColor
            val pathRibbon = Path().apply {
                moveTo(0.62f * w, 0f)
                cubicTo(0.75f * w, 0.045f * h, 0.77f * w, 0.13f * h, 0.84f * w, 0.195f * h)
                cubicTo(0.89f * w, 0.235f * h, 0.95f * w, 0.245f * h, w, 0.258f * h)
                lineTo(w, 0.145f * h)
                cubicTo(0.94f * w, 0.115f * h, 0.91f * w, 0.06f * h, 0.865f * w, 0f)
                close()
            }
            canvas.drawPath(pathRibbon, paint)

            // 3. Bottom-left double-S-curve wave mass
            paint.color = lowerWaveColor
            val pathLower = Path().apply {
                moveTo(0f, 0.362f * h)
                cubicTo(0.09f * w, 0.43f * h, 0.14f * w, 0.55f * h, 0.28f * w, 0.615f * h)
                cubicTo(0.42f * w, 0.675f * h, 0.54f * w, 0.695f * h, 0.61f * w, 0.81f * h)
                cubicTo(0.65f * w, 0.88f * h, 0.66f * w, 0.95f * h, 0.715f * w, h)
                lineTo(0f, h)
                close()
            }
            canvas.drawPath(pathLower, paint)

            // 4. Bottom-left nested corner accent wave
            paint.color = cornerPopColor
            val pathPop = Path().apply {
                moveTo(0f, 0.720f * h)
                cubicTo(0.065f * w, 0.755f * h, 0.075f * w, 0.835f * h, 0.14f * w, 0.870f * h)
                cubicTo(0.21f * w, 0.905f * h, 0.34f * w, 0.885f * h, 0.425f * w, h)
                lineTo(0f, h)
                close()
            }
            canvas.drawPath(pathPop, paint)
        }
    }

    // =========================================================================
    // 4. PIXEL_CLASSIC (Official Sample 1: Horizon Pebble, Wave & Tilted Card)
    // =========================================================================
    private fun renderPixelClassic(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        paint.color = colors.bgToned
        canvas.drawRect(0f, 0f, w, h, paint)

        val waveColor = when (colorVariant) {
            0 -> colors.primaryBold
            1 -> colors.primaryPastel
            else -> colors.tertiaryMid
        }
        val circleColor = when (colorVariant) {
            0 -> colors.secondaryMid
            1 -> colors.tertiaryPastel
            else -> colors.secondaryPastel
        }
        val cardColor = when (colorVariant) {
            0 -> colors.tertiaryMid
            1 -> colors.primaryBold
            else -> colors.primaryPastel
        }
        val dotColor = colors.accentPop

        withQuadrantTransform(canvas, w, h, layout) {
            // 1. Bottom-left two-lobed organic wave mass
            paint.color = waveColor
            val pathWave = Path().apply {
                moveTo(0f, 0.402f * h)
                cubicTo(0.08f * w, 0.42f * h, 0.14f * w, 0.465f * h, 0.26f * w, 0.485f * h)
                cubicTo(0.45f * w, 0.515f * h, 0.53f * w, 0.58f * h, 0.535f * w, 0.655f * h)
                cubicTo(0.54f * w, 0.72f * h, 0.625f * w, 0.765f * h, 0.605f * w, 0.84f * h)
                cubicTo(0.585f * w, 0.92f * h, 0.43f * w, 0.945f * h, 0.37f * w, h)
                lineTo(0f, h)
                close()
            }
            canvas.drawPath(pathWave, paint)

            // 2. Center focal circle
            paint.color = circleColor
            canvas.drawCircle(0.50f * w, 0.404f * h, 0.16f * w, paint)

            // 3. Crisp satellite dot
            paint.color = dotColor
            canvas.drawCircle(0.71f * w, 0.322f * h, 0.052f * w, paint)

            // 4. Right edge-cropped tilted rounded rectangle card
            paint.color = cardColor
            drawTiltedRoundRect(canvas, 0.905f * w, 0.542f * h, 0.44f * w, 0.355f * h, 0.075f * w, 13f, paint)
        }
    }

    // =========================================================================
    // 5. FLUID_WAVES (Opposing Organic Waves, Cookie Cloud & Focal Pebble)
    // =========================================================================
    private fun renderFluidWaves(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val upperWaveColor = when (colorVariant) {
            0 -> colors.secondarySoft
            1 -> colors.primarySoft
            else -> colors.tertiarySoft
        }
        val lowerWaveColor = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.tertiaryMid
            else -> colors.primaryBold
        }
        val pillColor = colors.tertiaryMid
        val circleColor = colors.secondaryMid

        withQuadrantTransform(canvas, w, h, layout) {
            // Top-right undulating wave
            paint.color = upperWaveColor
            val pathTop = Path().apply {
                moveTo(0.32f * w, 0f)
                cubicTo(0.42f * w, 0.09f * h, 0.62f * w, 0.11f * h, 0.68f * w, 0.21f * h)
                cubicTo(0.74f * w, 0.31f * h, 0.90f * w, 0.34f * h, w, 0.42f * h)
                lineTo(w, 0f)
                close()
            }
            canvas.drawPath(pathTop, paint)

            // Bottom-left two-lobed organic wave
            paint.color = lowerWaveColor
            val pathBot = Path().apply {
                moveTo(0f, 0.45f * h)
                cubicTo(0.10f * w, 0.47f * h, 0.20f * w, 0.52f * h, 0.32f * w, 0.56f * h)
                cubicTo(0.48f * w, 0.61f * h, 0.52f * w, 0.70f * h, 0.48f * w, 0.80f * h)
                cubicTo(0.44f * w, 0.90f * h, 0.56f * w, 0.94f * h, 0.62f * w, h)
                lineTo(0f, h)
                close()
            }
            canvas.drawPath(pathBot, paint)

            // Center-left focal circle + satellite dot
            paint.color = circleColor
            canvas.drawCircle(0.38f * w, 0.32f * h, 0.145f * w, paint)

            paint.color = colors.accentPop
            canvas.drawCircle(0.58f * w, 0.25f * h, 0.046f * w, paint)

            // Bottom-right tilted stadium pill
            paint.color = pillColor
            drawTiltedRoundRect(canvas, 0.86f * w, 0.74f * h, 0.54f * w, 0.32f * w, 0.16f * w, -28f, paint)
        }
    }

    // =========================================================================
    // 6. BAUHAUS_MODERN (Macro Architectural Dome, Stadium Pill & Squircle)
    // =========================================================================
    private fun renderBauhausModern(
        canvas: Canvas,
        w: Float,
        h: Float,
        colors: ResolvedWallpaperColors,
        paint: Paint,
        layout: Int,
        colorVariant: Int,
    ) {
        paint.color = colors.bgSoft
        canvas.drawRect(0f, 0f, w, h, paint)

        val domeColor = when (colorVariant) {
            0 -> colors.secondarySoft
            1 -> colors.primarySoft
            else -> colors.tertiarySoft
        }
        val pillColor = when (colorVariant) {
            0 -> colors.primaryPastel
            1 -> colors.tertiaryMid
            else -> colors.primaryBold
        }
        val squircleColor = when (colorVariant) {
            0 -> colors.tertiaryMid
            1 -> colors.primaryPastel
            else -> colors.neutralMid
        }

        withQuadrantTransform(canvas, w, h, layout) {
            // Top-right giant architectural dome
            paint.color = domeColor
            canvas.drawCircle(0.84f * w, 0.08f * h, 0.48f * w, paint)

            // Mid-left tilted stadium column
            paint.color = pillColor
            drawTiltedRoundRect(canvas, 0.06f * w, 0.46f * h, 0.42f * w, 0.34f * h, 0.21f * w, -16f, paint)

            // Bottom-right rotated squircle
            paint.color = squircleColor
            drawTiltedRoundRect(canvas, 0.76f * w, 0.86f * h, 0.56f * w, 0.56f * w, 0.15f * w, 36f, paint)

            // Center-right focal circle + crisp accent dot
            paint.color = colors.neutralMid
            canvas.drawCircle(0.58f * w, 0.48f * h, 0.14f * w, paint)

            paint.color = colors.accentPop
            canvas.drawCircle(0.42f * w, 0.36f * h, 0.046f * w, paint)
        }
    }
}
