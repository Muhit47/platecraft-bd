package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PlateEntity
import com.example.data.PlateRepository
import com.example.model.BanglaConstants
import com.example.model.BorderStyle
import com.example.model.PlateDesignConfig
import com.example.model.PlateTheme
import com.example.model.PlateType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlateEditorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlateRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = PlateRepository(database.plateDao())
    }

    val savedPlates: StateFlow<List<PlateEntity>> = repository.allPlates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _config = MutableStateFlow(PlateDesignConfig())
    val config: StateFlow<PlateDesignConfig> = _config.asStateFlow()

    private val _showPrintPreview = MutableStateFlow(false)
    val showPrintPreview: StateFlow<Boolean> = _showPrintPreview.asStateFlow()

    private val _showSavedPlates = MutableStateFlow(false)
    val showSavedPlates: StateFlow<Boolean> = _showSavedPlates.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun openPrintPreview() {
        _showPrintPreview.value = true
    }

    fun closePrintPreview() {
        _showPrintPreview.value = false
    }

    fun openSavedPlates() {
        _showSavedPlates.value = true
    }

    fun closeSavedPlates() {
        _showSavedPlates.value = false
    }

    fun setPlateType(type: PlateType) {
        _config.update { current ->
            // If switching to bike, recommend bike letters if current is car
            val newClass = if (type == PlateType.BIKE_FRONT || type == PlateType.BIKE_REAR) {
                if (current.classLetter in listOf("ক", "খ", "গ", "ঘ")) "ল" else current.classLetter
            } else if (type == PlateType.PRIVATE_CAR) {
                if (current.classLetter in listOf("ল", "হ", "ম")) "গ" else current.classLetter
            } else {
                current.classLetter
            }
            current.copy(plateType = type, classLetter = newClass)
        }
    }

    fun setMetro(metro: String) {
        _config.update { it.copy(metroName = metro) }
    }

    fun setClassLetter(classLetter: String) {
        _config.update { it.copy(classLetter = classLetter) }
    }

    fun setSeriesNumber(raw: String) {
        val cleaned = BanglaConstants.filterOnlyDigits(raw, maxLen = 2)
        _config.update { it.copy(seriesNumber = cleaned) }
    }

    fun setSerialNumber(raw: String) {
        val cleaned = BanglaConstants.filterOnlyDigits(raw, maxLen = 4)
        _config.update { it.copy(serialNumber = cleaned) }
    }

    fun setBloodGroup(group: String) {
        _config.update { it.copy(bloodGroup = group) }
    }

    fun setCustomTagline(tagline: String) {
        _config.update { it.copy(customTagline = tagline.take(30)) }
    }

    fun toggleEmblem(enabled: Boolean) {
        _config.update { it.copy(showEmblem = enabled) }
    }

    fun toggleHologram(enabled: Boolean) {
        _config.update { it.copy(showHologram = enabled) }
    }

    fun toggleScrewHoles(enabled: Boolean) {
        _config.update { it.copy(showScrewHoles = enabled) }
    }

    fun toggleCutGuides(enabled: Boolean) {
        _config.update { it.copy(showCutGuides = enabled) }
    }

    fun setTheme(theme: PlateTheme) {
        _config.update { it.copy(theme = theme) }
    }

    fun setBorderStyle(border: BorderStyle) {
        _config.update { it.copy(borderStyle = border) }
    }

    fun setCustomDimensions(widthMm: Float, heightMm: Float) {
        _config.update {
            it.copy(
                customWidthMm = widthMm.coerceIn(80f, 500f),
                customHeightMm = heightMm.coerceIn(30f, 300f)
            )
        }
    }

    fun saveCurrentPlate(title: String? = null) {
        viewModelScope.launch {
            val entity = PlateEntity.fromConfig(_config.value, title)
            repository.insert(entity)
            _userMessage.value = "Design saved to local library!"
        }
    }

    fun loadPlate(entity: PlateEntity) {
        _config.value = entity.toConfig()
        _showSavedPlates.value = false
        _userMessage.value = "Loaded: ${entity.title}"
    }

    fun deletePlate(entity: PlateEntity) {
        viewModelScope.launch {
            repository.delete(entity)
            _userMessage.value = "Plate removed from saved list"
        }
    }

    fun loadQuickPreset(preset: String) {
        when (preset) {
            "DHAKA_BIKE_FRONT" -> {
                _config.value = PlateDesignConfig(
                    plateType = PlateType.BIKE_FRONT,
                    metroName = "ঢাকা মেট্রো",
                    classLetter = "ল",
                    seriesNumber = "২৩",
                    serialNumber = "৮৮৯৯",
                    bloodGroup = "O+",
                    customTagline = "RIDE SAFE",
                    theme = PlateTheme.STANDARD_WHITE
                )
            }
            "DHAKA_BIKE_REAR" -> {
                _config.value = PlateDesignConfig(
                    plateType = PlateType.BIKE_REAR,
                    metroName = "ঢাকা মেট্রো",
                    classLetter = "হ",
                    seriesNumber = "৩৪",
                    serialNumber = "৫৬৭৮",
                    bloodGroup = "B+",
                    customTagline = "BIKERS BD",
                    theme = PlateTheme.STANDARD_WHITE
                )
            }
            "CAR_PRIVATE" -> {
                _config.value = PlateDesignConfig(
                    plateType = PlateType.PRIVATE_CAR,
                    metroName = "ঢাকা মেট্রো",
                    classLetter = "গ",
                    seriesNumber = "১৫",
                    serialNumber = "১২৩৪",
                    bloodGroup = "None",
                    customTagline = "",
                    theme = PlateTheme.STANDARD_WHITE
                )
            }
            "CHATTOGRAM_STEALTH" -> {
                _config.value = PlateDesignConfig(
                    plateType = PlateType.BIKE_FRONT,
                    metroName = "চট্টগ্রাম মেট্রো",
                    classLetter = "ল",
                    seriesNumber = "১৯",
                    serialNumber = "৭৭৮৮",
                    bloodGroup = "A+",
                    customTagline = "SHOWROOM EDITION",
                    theme = PlateTheme.STEALTH_BLACK,
                    borderStyle = BorderStyle.DOUBLE_BORDER
                )
            }
        }
        _userMessage.value = "Preset applied!"
    }
}
