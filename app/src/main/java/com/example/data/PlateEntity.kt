package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.BorderStyle
import com.example.model.PlateDesignConfig
import com.example.model.PlateTheme
import com.example.model.PlateType

@Entity(tableName = "saved_plates")
data class PlateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val plateType: String,
    val widthMm: Float,
    val heightMm: Float,
    val metroName: String,
    val classLetter: String,
    val seriesNumber: String,
    val serialNumber: String,
    val bloodGroup: String,
    val customTagline: String,
    val showEmblem: Boolean,
    val showHologram: Boolean,
    val showScrewHoles: Boolean,
    val showCutGuides: Boolean,
    val themeName: String,
    val borderStyleName: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toConfig(): PlateDesignConfig {
        val type = try {
            PlateType.valueOf(plateType)
        } catch (e: Exception) {
            PlateType.BIKE_FRONT
        }
        val theme = try {
            PlateTheme.valueOf(themeName)
        } catch (e: Exception) {
            PlateTheme.STANDARD_WHITE
        }
        val border = try {
            BorderStyle.valueOf(borderStyleName)
        } catch (e: Exception) {
            BorderStyle.STANDARD_EMBOSSED
        }
        return PlateDesignConfig(
            plateType = type,
            customWidthMm = widthMm,
            customHeightMm = heightMm,
            metroName = metroName,
            classLetter = classLetter,
            seriesNumber = seriesNumber,
            serialNumber = serialNumber,
            bloodGroup = bloodGroup,
            customTagline = customTagline,
            showEmblem = showEmblem,
            showHologram = showHologram,
            showScrewHoles = showScrewHoles,
            showCutGuides = showCutGuides,
            theme = theme,
            borderStyle = border
        )
    }

    companion object {
        fun fromConfig(config: PlateDesignConfig, customTitle: String? = null): PlateEntity {
            val title = customTitle ?: "${config.plateType.title} (${config.fullPlateNumberFormatted})"
            return PlateEntity(
                title = title,
                plateType = config.plateType.name,
                widthMm = config.effectiveWidthMm,
                heightMm = config.effectiveHeightMm,
                metroName = config.metroName,
                classLetter = config.classLetter,
                seriesNumber = config.seriesNumber,
                serialNumber = config.serialNumber,
                bloodGroup = config.bloodGroup,
                customTagline = config.customTagline,
                showEmblem = config.showEmblem,
                showHologram = config.showHologram,
                showScrewHoles = config.showScrewHoles,
                showCutGuides = config.showCutGuides,
                themeName = config.theme.name,
                borderStyleName = config.borderStyle.name
            )
        }
    }
}
