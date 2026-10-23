package com.example.model

data class PlateDesignConfig(
    val plateType: PlateType = PlateType.BIKE_FRONT,
    val customWidthMm: Float = 250f,
    val customHeightMm: Float = 100f,
    val metroName: String = "ঢাকা মেট্রো",
    val classLetter: String = "ল",
    val seriesNumber: String = "১১",
    val serialNumber: String = "১২৩৪",
    val bloodGroup: String = "None",
    val customTagline: String = "",
    val showEmblem: Boolean = true,
    val showHologram: Boolean = true,
    val showScrewHoles: Boolean = true,
    val showCutGuides: Boolean = true,
    val theme: PlateTheme = PlateTheme.STANDARD_WHITE,
    val borderStyle: BorderStyle = BorderStyle.STANDARD_EMBOSSED
) {
    val effectiveWidthMm: Float
        get() = if (plateType == PlateType.CUSTOM) customWidthMm else plateType.widthMm

    val effectiveHeightMm: Float
        get() = if (plateType == PlateType.CUSTOM) customHeightMm else plateType.heightMm

    val aspectRatio: Float
        get() = (effectiveWidthMm / effectiveHeightMm).coerceIn(0.5f, 8.0f)

    val displayMetroName: String
        get() = BanglaConstants.normalizeMetroName(metroName)

    /**
     * E.g. "ঢাকা মেট্রো-ল-১১-১২৩৪" or "নওয়াবগঞ্জ-ল-১১-১২৩৪"
     */
    val fullPlateNumberFormatted: String
        get() {
            val seriesClean = if (seriesNumber.isNotBlank()) "-$seriesNumber" else ""
            val serialClean = if (serialNumber.isNotBlank()) "-$serialNumber" else ""
            return "$displayMetroName-$classLetter$seriesClean$serialClean"
        }

    /**
     * Top line for stacked plates (e.g. "ঢাকা মেট্রো-ল")
     */
    val topLineText: String
        get() = "$displayMetroName-$classLetter"

    /**
     * Bottom line for stacked plates (e.g. "১১-১২৩৪")
     */
    val bottomLineText: String
        get() = "$seriesNumber-$serialNumber"

    /**
     * For elongated plates like Bike Front (285 x 50mm), everything is in one clean single line:
     * "ঢাকা মেট্রো-ল-১১-১২৩৪"
     */
    val isSingleLineLayout: Boolean
        get() = plateType == PlateType.BIKE_FRONT || (effectiveWidthMm / effectiveHeightMm >= 3.5f)
}
