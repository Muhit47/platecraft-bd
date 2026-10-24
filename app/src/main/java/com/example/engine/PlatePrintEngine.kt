package com.example.engine

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import androidx.core.content.FileProvider
import com.example.model.BorderStyle
import com.example.model.PlateDesignConfig
import com.example.model.PlateTheme
import com.example.model.PlateType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PlatePrintEngine {

    // 1 mm in PostScript points (72 points / 25.4 mm)
    const val POINTS_PER_MM = 72f / 25.4f

    // Standard A4 dimensions in points: 210 mm x 297 mm
    const val A4_SHORT_EDGE_PT = (210f * POINTS_PER_MM).toInt() // ~595 pt
    const val A4_LONG_EDGE_PT = (297f * POINTS_PER_MM).toInt()  // ~842 pt

    // 300 DPI scale: 300 dots / 25.4 mm = ~11.811 pixels per mm
    const val PIXELS_PER_MM_300DPI = 300f / 25.4f

    private var cachedBanglaTypeface: Typeface? = null

    fun getBanglaTypeface(context: Context? = null): Typeface {
        if (cachedBanglaTypeface == null && context != null) {
            try {
                cachedBanglaTypeface = androidx.core.content.res.ResourcesCompat.getFont(context, com.example.R.font.kalpurush)
            } catch (e: Exception) {
                try {
                    cachedBanglaTypeface = androidx.core.content.res.ResourcesCompat.getFont(context, com.example.R.font.hind_siliguri)
                } catch (e2: Exception) {
                    cachedBanglaTypeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }
        }
        return cachedBanglaTypeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    /**
     * Determines whether Landscape orientation is needed so that wide plates
     * (e.g. Bike Front 285 mm, Private Car 300 mm, Scooter 200 mm) fit properly without clipping.
     */
    fun isLandscapeRequired(config: PlateDesignConfig): Boolean {
        return config.effectiveWidthMm > 195f || (config.effectiveWidthMm > config.effectiveHeightMm * 1.25f)
    }

    fun getA4PageWidthPt(config: PlateDesignConfig): Int {
        return if (isLandscapeRequired(config)) A4_LONG_EDGE_PT else A4_SHORT_EDGE_PT
    }

    fun getA4PageHeightPt(config: PlateDesignConfig): Int {
        return if (isLandscapeRequired(config)) A4_SHORT_EDGE_PT else A4_LONG_EDGE_PT
    }

    /**
     * Generates a 1:1 true physical scale A4 PDF with the number plate centered,
     * using Landscape A4 for wide plates to avoid clipping, including cutting guides and calipers.
     */
    fun createA4PdfFile(context: Context, config: PlateDesignConfig): File {
        val pdfDir = File(context.cacheDir, "plates").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "Plate_${config.plateType.name}_$timeStamp.pdf"
        val outputFile = File(pdfDir, fileName)

        val isLandscape = isLandscapeRequired(config)
        val pageWidth = if (isLandscape) A4_LONG_EDGE_PT else A4_SHORT_EDGE_PT
        val pageHeight = if (isLandscape) A4_SHORT_EDGE_PT else A4_LONG_EDGE_PT

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = document.startPage(pageInfo)

        drawA4PrintPage(page.canvas, config, pageWidth.toFloat(), pageHeight.toFloat(), getBanglaTypeface(context), context)

        document.finishPage(page)

        FileOutputStream(outputFile).use { out ->
            document.writeTo(out)
        }
        document.close()

        return outputFile
    }


    /**
     * Draws the full A4 layout: Header instructions, 1:1 scaled plate, cut lines, crop marks,
     * mounting drill holes, and metadata footer.
     */
    fun drawA4PrintPage(
        canvas: Canvas,
        config: PlateDesignConfig,
        pageWidthPt: Float,
        pageHeightPt: Float,
        customTypeface: Typeface? = null,
        context: Context? = null
    ) {
        // Fill white background for A4 sheet
        canvas.drawColor(AndroidColor.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Header Information & Scale Warning
        paint.color = AndroidColor.rgb(15, 23, 42) // Slate 900
        paint.textSize = 13f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("PlateCraft BD — 1:1 True Physical Scale Number Plate", pageWidthPt / 2f, 32f, paint)

        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = AndroidColor.rgb(71, 85, 105) // Slate 600
        canvas.drawText(
            "PRINTER SETTING: Select \"Actual Size\" or \"100% Scale\" (Do NOT select \"Fit to Page\") for exact millimeters.",
            pageWidthPt / 2f,
            46f,
            paint
        )

        // Target Plate Dimensions in points
        val plateWidthPt = config.effectiveWidthMm * POINTS_PER_MM
        val plateHeightPt = config.effectiveHeightMm * POINTS_PER_MM

        // Check if plate exceeds printable bounds (e.g. 300mm on A4) and compute safe fit scale if needed
        val maxAvailableW = pageWidthPt - 24f // 12pt (~4.2mm) safety border
        val maxAvailableH = pageHeightPt - 130f
        val scaleX = if (plateWidthPt > maxAvailableW) maxAvailableW / plateWidthPt else 1.0f
        val scaleY = if (plateHeightPt > maxAvailableH) maxAvailableH / plateHeightPt else 1.0f
        val fitScale = minOf(scaleX, scaleY).coerceAtMost(1.0f)

        val finalW = plateWidthPt * fitScale
        val finalH = plateHeightPt * fitScale

        // Plate bounds centered horizontally, positioned cleanly on page
        val plateLeft = (pageWidthPt - finalW) / 2f
        val plateTop = if (pageHeightPt < 650f) 72f else 110f
        val plateRight = plateLeft + finalW
        val plateBottom = plateTop + finalH

        val plateRect = RectF(plateLeft, plateTop, plateRight, plateBottom)

        // 2. Draw Crop Marks & Cutting Guides
        if (config.showCutGuides) {
            drawCropMarks(canvas, plateRect)
        }

        // 3. Draw The Number Plate Itself with authentic Bengali font
        drawPlateToCanvas(canvas, plateRect, config, isPrintMode = true, customTypeface = customTypeface, context = context)

        // 4. Dimension Calipers / Rulers
        drawDimensionLabels(canvas, plateRect, config, fitScale)

        // 5. Technical Specifications Box below
        val specBoxTop = plateBottom + 40f
        if (specBoxTop + 85f <= pageHeightPt) {
            drawSpecSummaryBox(canvas, specBoxTop, pageWidthPt, config)
        }
    }


    private fun drawCropMarks(canvas: Canvas, rect: RectF) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.rgb(100, 116, 139) // Slate 500
            strokeWidth = 0.75f
            style = Paint.Style.STROKE
        }

        val markLen = 6f * POINTS_PER_MM // 6mm crop extension
        val gap = 1.5f * POINTS_PER_MM

        // Top-Left crop
        canvas.drawLine(rect.left - gap - markLen, rect.top, rect.left - gap, rect.top, paint)
        canvas.drawLine(rect.left, rect.top - gap - markLen, rect.left, rect.top - gap, paint)

        // Top-Right crop
        canvas.drawLine(rect.right + gap, rect.top, rect.right + gap + markLen, rect.top, paint)
        canvas.drawLine(rect.right, rect.top - gap - markLen, rect.right, rect.top - gap, paint)

        // Bottom-Left crop
        canvas.drawLine(rect.left - gap - markLen, rect.bottom, rect.left - gap, rect.bottom, paint)
        canvas.drawLine(rect.left, rect.bottom + gap, rect.left, rect.bottom + gap + markLen, paint)

        // Bottom-Right crop
        canvas.drawLine(rect.right + gap, rect.bottom, rect.right + gap + markLen, rect.bottom, paint)
        canvas.drawLine(rect.right, rect.bottom + gap, rect.right, rect.bottom + gap + markLen, paint)

        // Dashed Cut Boundary Line around plate
        val dashedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.rgb(203, 213, 225)
            strokeWidth = 0.5f
            style = Paint.Style.STROKE
            pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
        }
        canvas.drawRect(rect, dashedPaint)
    }

    private fun drawDimensionLabels(canvas: Canvas, rect: RectF, config: PlateDesignConfig, fitScale: Float = 1.0f) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AndroidColor.rgb(100, 116, 139)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }

        // Width label above
        val scaleNote = if (fitScale < 0.999f) " (Scaled ${(fitScale * 100).toInt()}% to fit A4)" else " (1:1 True Scale)"
        val widthText = "◄──  ${config.effectiveWidthMm.toInt()} mm$scaleNote  ──►"
        canvas.drawText(widthText, rect.centerX(), rect.top - 8f, paint)

        // Height label on the side
        canvas.save()
        canvas.rotate(-90f, rect.left - 10f, rect.centerY())
        val heightText = "◄── ${config.effectiveHeightMm.toInt()} mm ──►"
        canvas.drawText(heightText, rect.left - 10f, rect.centerY() - 2f, paint)
        canvas.restore()
    }

    private fun drawSpecSummaryBox(canvas: Canvas, topY: Float, pageWidthPt: Float, config: PlateDesignConfig) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val boxWidth = 440f
        val boxLeft = (pageWidthPt - boxWidth) / 2f
        val boxRect = RectF(boxLeft, topY, boxLeft + boxWidth, topY + 110f)

        // Box background
        paint.color = AndroidColor.rgb(248, 250, 252)
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(boxRect, 6f, 6f, paint)

        // Box border
        paint.color = AndroidColor.rgb(226, 232, 240)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(boxRect, 6f, 6f, paint)

        // Title
        paint.style = Paint.Style.FILL
        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Plate & Vehicle Specifications", boxLeft + 16f, topY + 22f, paint)

        // Details
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = AndroidColor.rgb(71, 85, 105)

        val col1X = boxLeft + 16f
        val col2X = boxLeft + 230f

        canvas.drawText("Vehicle Type: ${config.plateType.title}", col1X, topY + 42f, paint)
        canvas.drawText("Physical Dimensions: ${config.effectiveWidthMm.toInt()} × ${config.effectiveHeightMm.toInt()} mm", col1X, topY + 58f, paint)
        canvas.drawText("Plate Registration: ${config.fullPlateNumberFormatted}", col1X, topY + 74f, paint)
        canvas.drawText("Plate Finish: ${config.theme.title}", col1X, topY + 90f, paint)

        val bloodInfo = if (config.bloodGroup != "None") config.bloodGroup else "Not specified"
        canvas.drawText("Emergency Blood Group: $bloodInfo", col2X, topY + 42f, paint)
        val emblemInfo = if (config.showEmblem) "National Flag & Hologram" else "Disabled"
        canvas.drawText("Security Emblem: $emblemInfo", col2X, topY + 58f, paint)
        val holesInfo = if (config.showScrewHoles) "2 Standard Mounting Points" else "No Holes"
        canvas.drawText("Mounting Drill Points: $holesInfo", col2X, topY + 74f, paint)
        val taglineInfo = if (config.customTagline.isNotBlank()) config.customTagline else "None"
        canvas.drawText("Rider Tag / Motto: $taglineInfo", col2X, topY + 90f, paint)
    }

    /**
     * Core renderer: draws the actual vehicle plate onto any canvas given its destination rectangle.
     * Used by A4 PDF print, high-res bitmap export, and Compose Canvas preview.
     */
    fun drawPlateToCanvas(
        canvas: Canvas,
        rect: RectF,
        config: PlateDesignConfig,
        isPrintMode: Boolean = false,
        customTypeface: Typeface? = null,
        context: Context? = null
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val cornerRadius = (rect.height() * 0.06f).coerceIn(4f, 16f)

        // 1. Plate Background Color
        val bgIntColor = when (config.theme) {
            PlateTheme.STANDARD_WHITE -> AndroidColor.rgb(252, 252, 253)
            PlateTheme.COMMERCIAL_YELLOW -> AndroidColor.rgb(255, 212, 0)
            PlateTheme.ELECTRIC_GREEN -> AndroidColor.rgb(0, 106, 78)
            PlateTheme.STEALTH_BLACK -> AndroidColor.rgb(20, 20, 24)
            PlateTheme.CARBON_DARK -> AndroidColor.rgb(36, 38, 43)
        }
        paint.color = bgIntColor
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        // Subtle realistic specular gradient reflection sheen
        if (!isPrintMode && config.theme.isRetroReflective) {
            val sheenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.argb(18, 255, 255, 255)
                style = Paint.Style.FILL
            }
            val sheenRect = RectF(rect.left, rect.top, rect.right, rect.top + (rect.height() * 0.45f))
            canvas.drawRoundRect(sheenRect, cornerRadius, cornerRadius, sheenPaint)
        }

        // 2. Outer Border & Stamped Aluminum Frame
        val borderIntColor = when (config.theme) {
            PlateTheme.STANDARD_WHITE, PlateTheme.COMMERCIAL_YELLOW -> AndroidColor.rgb(15, 23, 42)
            PlateTheme.ELECTRIC_GREEN -> AndroidColor.rgb(255, 255, 255)
            PlateTheme.STEALTH_BLACK -> AndroidColor.rgb(82, 82, 91)
            PlateTheme.CARBON_DARK -> AndroidColor.rgb(148, 163, 184)
        }

        val strokeThick = (rect.height() * 0.028f).coerceIn(1.8f, 6.0f)

        when (config.borderStyle) {
            BorderStyle.STANDARD_EMBOSSED -> {
                // Embossed raised outer rim
                paint.color = borderIntColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeThick * 1.4f
                val inset = strokeThick * 0.9f
                val frameRect = RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset)
                canvas.drawRoundRect(frameRect, cornerRadius * 0.82f, cornerRadius * 0.82f, paint)

                // Inner embossed subtle highlight
                if (!isPrintMode) {
                    paint.color = AndroidColor.argb(35, 255, 255, 255)
                    paint.strokeWidth = 0.8f
                    val innerHighlight = RectF(frameRect.left + 1f, frameRect.top + 1f, frameRect.right - 1f, frameRect.bottom - 1f)
                    canvas.drawRoundRect(innerHighlight, cornerRadius * 0.8f, cornerRadius * 0.8f, paint)
                }
            }
            BorderStyle.DOUBLE_BORDER -> {
                paint.color = borderIntColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeThick
                val inset1 = strokeThick
                canvas.drawRoundRect(RectF(rect.left + inset1, rect.top + inset1, rect.right - inset1, rect.bottom - inset1), cornerRadius * 0.82f, cornerRadius * 0.82f, paint)

                paint.strokeWidth = strokeThick * 0.5f
                val inset2 = strokeThick * 2.6f
                canvas.drawRoundRect(RectF(rect.left + inset2, rect.top + inset2, rect.right - inset2, rect.bottom - inset2), cornerRadius * 0.68f, cornerRadius * 0.68f, paint)
            }
            BorderStyle.THIN_BORDER -> {
                paint.color = borderIntColor
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = strokeThick * 0.7f
                val inset = strokeThick * 0.5f
                canvas.drawRoundRect(RectF(rect.left + inset, rect.top + inset, rect.right - inset, rect.bottom - inset), cornerRadius * 0.82f, cornerRadius * 0.82f, paint)
            }
            BorderStyle.NO_BORDER -> {
                // Frameless
            }
        }

        // 3. Mounting Bolt / Screw Holes (Realistic Metallic Rivet styling)
        if (config.showScrewHoles) {
            drawScrewHoles(canvas, rect, strokeThick, isPrintMode)
        }

        // 4. Bangladesh National Flag Circle Emblem & Hologram
        val contentLeftInset: Float = if (config.showEmblem) {
            drawEmblemAndBadges(canvas, rect, config, context)
        } else {
            rect.left + (rect.width() * 0.05f)
        }

        // 5. Text Drawing: Dual Layout (Single-Line vs Stacked 2-Line)
        val textIntColor = when (config.theme) {
            PlateTheme.STANDARD_WHITE, PlateTheme.COMMERCIAL_YELLOW -> AndroidColor.rgb(15, 23, 42)
            PlateTheme.ELECTRIC_GREEN, PlateTheme.STEALTH_BLACK, PlateTheme.CARBON_DARK -> AndroidColor.rgb(255, 255, 255)
        }

        if (config.isSingleLineLayout) {
            drawSingleLinePlateText(canvas, rect, config, contentLeftInset, textIntColor, isPrintMode, customTypeface)
        } else {
            drawStackedPlateText(canvas, rect, config, contentLeftInset, textIntColor, isPrintMode, customTypeface)
        }

        // 6. Blood Group Badge in Corner
        if (config.bloodGroup != "None" && config.bloodGroup.isNotBlank()) {
            drawBloodGroupBadge(canvas, rect, config.bloodGroup)
        }


        // 7. Custom Tagline in Footer (if present)
        if (config.customTagline.isNotBlank() && !config.isSingleLineLayout) {
            drawTagline(canvas, rect, config.customTagline, textIntColor)
        }
    }

    private fun drawScrewHoles(canvas: Canvas, rect: RectF, strokeThick: Float, isPrintMode: Boolean) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val holeRadius = (rect.height() * 0.038f).coerceIn(2.5f, 5.5f)
        val xInset = rect.width() * 0.14f
        val yCenter = if (rect.height() > 80f) rect.top + (rect.height() * 0.14f) else rect.centerY()

        val leftHoleX = rect.left + xInset
        val rightHoleX = rect.right - xInset

        // Outer washer ring
        paint.color = if (isPrintMode) AndroidColor.rgb(148, 163, 184) else AndroidColor.rgb(203, 213, 225)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = strokeThick * 0.5f

        canvas.drawCircle(leftHoleX, yCenter, holeRadius * 1.5f, paint)
        canvas.drawCircle(rightHoleX, yCenter, holeRadius * 1.5f, paint)

        // Center drill hole
        paint.style = Paint.Style.FILL
        paint.color = if (isPrintMode) AndroidColor.WHITE else AndroidColor.rgb(71, 85, 105)
        canvas.drawCircle(leftHoleX, yCenter, holeRadius, paint)
        canvas.drawCircle(rightHoleX, yCenter, holeRadius, paint)

        if (isPrintMode) {
            // Precise crosshair for manual drilling alignment in print
            val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = AndroidColor.rgb(100, 116, 139)
                strokeWidth = 0.5f
            }
            canvas.drawLine(leftHoleX - holeRadius * 1.6f, yCenter, leftHoleX + holeRadius * 1.6f, yCenter, crossPaint)
            canvas.drawLine(leftHoleX, yCenter - holeRadius * 1.6f, leftHoleX, yCenter + holeRadius * 1.6f, crossPaint)

            canvas.drawLine(rightHoleX - holeRadius * 1.6f, yCenter, rightHoleX + holeRadius * 1.6f, yCenter, crossPaint)
            canvas.drawLine(rightHoleX, yCenter - holeRadius * 1.6f, rightHoleX, yCenter + holeRadius * 1.6f, crossPaint)
        }
    }

    private fun drawEmblemAndBadges(canvas: Canvas, rect: RectF, config: PlateDesignConfig, context: Context? = null): Float {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val emblemRadius = (rect.height() * 0.16f).coerceIn(6f, 18f)
        val emblemCenterX = rect.left + (rect.width() * 0.05f) + emblemRadius
        val emblemCenterY = rect.centerY()

        // Load BRTA emblem
        val emblemBitmap = context?.resources?.let { res ->
            android.graphics.BitmapFactory.decodeResource(res, com.example.R.drawable.ic_brta_logo)
        }
        val emblemSize = emblemRadius * 3.4f
        val emblemRect = RectF(
            emblemCenterX - emblemSize / 2f,
            emblemCenterY - emblemSize / 2f,
            emblemCenterX + emblemSize / 2f,
            emblemCenterY + emblemSize / 2f
        )
        if (emblemBitmap != null) {
            val filterPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            canvas.drawBitmap(emblemBitmap, null, emblemRect, filterPaint)
        } else {
            // Fallback to old drawing if bitmap fails
            paint.style = Paint.Style.FILL
            paint.color = AndroidColor.rgb(0, 106, 78)
            canvas.drawCircle(emblemCenterX, emblemCenterY, emblemRadius, paint)
            paint.color = AndroidColor.rgb(244, 42, 65)
            canvas.drawCircle(emblemCenterX, emblemCenterY, emblemRadius * 0.55f, paint)
        }

        // Digital Hologram representation (if enabled)
        if (config.showHologram) {
            val holoWidth = emblemRadius * 1.4f
            val holoHeight = emblemRadius * 0.8f
            val holoRect = RectF(
                emblemCenterX - holoWidth / 2f,
                emblemCenterY + emblemRadius + 3f,
                emblemCenterX + holoWidth / 2f,
                emblemCenterY + emblemRadius + 3f + holoHeight
            )
            // Digital Hologram representation disabled by user request
            // if (holoRect.bottom < rect.bottom - 4f) {
            //     paint.color = AndroidColor.rgb(56, 189, 248) // Holographic Cyan
            //     paint.style = Paint.Style.STROKE
            //     paint.strokeWidth = 1f
            //     canvas.drawRoundRect(holoRect, 2f, 2f, paint)
            //
            //     paint.style = Paint.Style.FILL
            //     paint.textSize = (holoHeight * 0.6f).coerceAtLeast(4f)
            //     paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            //     paint.textAlign = Paint.Align.CENTER
            //     paint.color = AndroidColor.rgb(14, 165, 233)
            //     // Generic hologram marker
            //     canvas.drawRect(holoRect.left + 2f, holoRect.top + 2f, holoRect.right - 2f, holoRect.bottom - 2f, paint)
            // }
        }

        return emblemCenterX + emblemRadius + (rect.width() * 0.03f)
    }

    private fun drawSingleLinePlateText(
        canvas: Canvas,
        rect: RectF,
        config: PlateDesignConfig,
        contentLeft: Float,
        textColor: Int,
        isPrintMode: Boolean,
        customTypeface: Typeface? = null
    ) {
        val selectedTypeface = customTypeface ?: cachedBanglaTypeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            typeface = selectedTypeface
            textAlign = Paint.Align.CENTER
            if (!isPrintMode) {
                // Subtle 3D stamped shadow
                setShadowLayer(1.2f, 0f, 0.8f, AndroidColor.argb(30, 0, 0, 0))
            }
        }

        // Format: "ঢাকা মেট্রো-ল-১১-১২৩৪" or clean spaced layout
        val fullText = config.fullPlateNumberFormatted
        val availableWidth = (rect.right - (rect.width() * 0.05f)) - contentLeft
        val idealTextSize = rect.height() * 0.52f

        paint.textSize = idealTextSize
        var measuredW = paint.measureText(fullText)
        if (measuredW > availableWidth && measuredW > 0) {
            paint.textSize = idealTextSize * (availableWidth / measuredW) * 0.95f
        }

        val textCenterX = (contentLeft + rect.right - (rect.width() * 0.03f)) / 2f
        val textY = rect.centerY() - ((paint.descent() + paint.ascent()) / 2f)

        canvas.drawText(fullText, textCenterX, textY, paint)
    }

    private fun drawStackedPlateText(
        canvas: Canvas,
        rect: RectF,
        config: PlateDesignConfig,
        contentLeft: Float,
        textColor: Int,
        isPrintMode: Boolean,
        customTypeface: Typeface? = null
    ) {
        val selectedTypeface = customTypeface ?: cachedBanglaTypeface ?: Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            typeface = selectedTypeface
            textAlign = Paint.Align.CENTER
            if (!isPrintMode) {
                setShadowLayer(1.5f, 0f, 1f, AndroidColor.argb(30, 0, 0, 0))
            }
        }

        val topLine = config.topLineText       // "ঢাকা মেট্রো-ল"
        val bottomLine = config.bottomLineText // "১১-১২৩৪"

        val availableWidth = (rect.right - (rect.width() * 0.05f)) - contentLeft
        val textCenterX = (contentLeft + rect.right - (rect.width() * 0.03f)) / 2f

        // Top line font size (Metro & Class)
        val topSize = rect.height() * 0.28f
        paint.textSize = topSize
        var topMeasured = paint.measureText(topLine)
        if (topMeasured > availableWidth && topMeasured > 0) {
            paint.textSize = topSize * (availableWidth / topMeasured) * 0.95f
        }
        val topY = rect.top + (rect.height() * 0.40f)
        canvas.drawText(topLine, textCenterX, topY, paint)

        // Bottom line font size (Series & Serial numerals - large bold)
        val bottomSize = rect.height() * 0.42f
        paint.textSize = bottomSize
        var bottomMeasured = paint.measureText(bottomLine)
        if (bottomMeasured > availableWidth && bottomMeasured > 0) {
            paint.textSize = bottomSize * (availableWidth / bottomMeasured) * 0.95f
        }
        val bottomY = rect.top + (rect.height() * 0.82f)
        canvas.drawText(bottomLine, textCenterX, bottomY, paint)
    }

    private fun drawBloodGroupBadge(canvas: Canvas, rect: RectF, bloodGroup: String) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val badgeH = (rect.height() * 0.18f).coerceIn(10f, 22f)
        val badgeW = badgeH * 2.2f
        val badgeRight = rect.right - (rect.width() * 0.035f)
        val badgeTop = rect.top + (rect.height() * 0.06f)
        val badgeRect = RectF(badgeRight - badgeW, badgeTop, badgeRight, badgeTop + badgeH)

        // Red badge pill
        paint.color = AndroidColor.rgb(225, 29, 72) // Crimson
        paint.style = Paint.Style.FILL
        canvas.drawRoundRect(badgeRect, badgeH / 2f, badgeH / 2f, paint)

        // White Blood Group text
        paint.color = AndroidColor.WHITE
        paint.textSize = badgeH * 0.65f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        val textY = badgeRect.centerY() - ((paint.descent() + paint.ascent()) / 2f)
        canvas.drawText(bloodGroup, badgeRect.centerX(), textY, paint)
    }

    private fun drawTagline(canvas: Canvas, rect: RectF, tagline: String, textColor: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = textColor
            alpha = 180
            textSize = (rect.height() * 0.08f).coerceIn(6f, 12f)
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        val taglineY = rect.bottom - (rect.height() * 0.04f)
        canvas.drawText(tagline.uppercase(Locale.getDefault()), rect.centerX(), taglineY, paint)
    }

    /**
     * Exports a 300+ DPI high-resolution bitmap suitable for photo labs and vinyl cutting plotters.
     */
    fun createHighResBitmap(config: PlateDesignConfig, context: Context? = null): Bitmap {
        val widthPx = (config.effectiveWidthMm * PIXELS_PER_MM_300DPI).toInt().coerceAtLeast(300)
        val heightPx = (config.effectiveHeightMm * PIXELS_PER_MM_300DPI).toInt().coerceAtLeast(100)

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val plateRect = RectF(0f, 0f, widthPx.toFloat(), heightPx.toFloat())

        val tf = context?.let { getBanglaTypeface(it) } ?: cachedBanglaTypeface
        drawPlateToCanvas(canvas, plateRect, config, isPrintMode = false, customTypeface = tf, context = context)
        return bitmap
    }


    /**
     * Saves high-res image to file and returns File for sharing or opening.
     */
    fun saveBitmapToFile(context: Context, bitmap: Bitmap, config: PlateDesignConfig): File {
        val dir = File(context.cacheDir, "plates").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(dir, "Plate_${config.plateType.name}_$timeStamp.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file
    }

    /**
     * Triggers native Android system printing dialog via PrintManager.
     */
    fun printPlateDirectly(context: Context, config: PlateDesignConfig) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager ?: return
        val jobName = "PlateCraft BD - ${config.fullPlateNumberFormatted}"

        val adapter = object : PrintDocumentAdapter() {
            private var generatedPdf: File? = null

            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }

                val info = PrintDocumentInfo.Builder(jobName)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(1)
                    .build()

                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                try {
                    val pdfFile = createA4PdfFile(context, config)
                    generatedPdf = pdfFile

                    FileInputStream(pdfFile).use { input ->
                        FileOutputStream(destination?.fileDescriptor).use { output ->
                            input.copyTo(output)
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }

        val isLandscape = isLandscapeRequired(config)
        val mediaSize = if (isLandscape) {
            PrintAttributes.MediaSize.ISO_A4.asLandscape()
        } else {
            PrintAttributes.MediaSize.ISO_A4.asPortrait()
        }

        val attributes = PrintAttributes.Builder()
            .setMediaSize(mediaSize)
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()

        printManager.print(jobName, adapter, attributes)
    }

    /**
     * Creates an Intent to share the generated PDF file via Android ShareSheet.
     */
    fun createSharePdfIntent(context: Context, pdfFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            pdfFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "1:1 True Scale Bangladesh Vehicle Number Plate")
            putExtra(
                Intent.EXTRA_TEXT,
                "Print this PDF on standard A4 paper at 100% scale (no scaling) for exact physical dimensions."
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    /**
     * Creates an Intent to share the generated high-res PNG image.
     */
    fun createShareImageIntent(context: Context, imageFile: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "High-Res Vehicle Number Plate")
            putExtra(Intent.EXTRA_TEXT, "High-resolution vehicle license plate design generated with PlateCraft BD.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
