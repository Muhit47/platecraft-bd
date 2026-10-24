package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BanglaConstants
import com.example.model.PlateDesignConfig
import com.example.model.PlateType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PlateCraft BD", appName)
    }

    @Test
    fun `digit conversion to bangla numerals works accurately`() {
        val englishInput = "11-9999"
        val banglaOutput = BanglaConstants.toBanglaDigits(englishInput)
        assertEquals("১১-৯৯৯৯", banglaOutput)

        val mixedInput = "Dhaka 12-3456"
        val mixedBangla = BanglaConstants.toBanglaDigits(mixedInput)
        assertEquals("Dhaka ১২-৩৪৫৬", mixedBangla)

        val filtered = BanglaConstants.filterOnlyDigits("ab 12 cd", maxLen = 2)
        assertEquals("১২", filtered)
    }

    @Test
    fun `plate config formats single line for bike front and stacked for rear`() {
        val frontConfig = PlateDesignConfig(
            plateType = PlateType.BIKE_FRONT,
            metroName = "ঢাকা মেট্রো",
            classLetter = "ল",
            seriesNumber = "১১",
            serialNumber = "১২৩৪"
        )
        assertTrue(frontConfig.isSingleLineLayout)
        assertEquals("ঢাকা মেট্রো-ল-১১-১২৩৪", frontConfig.fullPlateNumberFormatted)

        val rearConfig = PlateDesignConfig(
            plateType = PlateType.BIKE_REAR,
            metroName = "ঢাকা মেট্রো",
            classLetter = "হ",
            seriesNumber = "২২",
            serialNumber = "৫৬৭৮"
        )
        assertEquals(false, rearConfig.isSingleLineLayout)
        assertEquals("ঢাকা মেট্রো-হ", rearConfig.topLineText)
        assertEquals("২২-৫৬৭৮", rearConfig.bottomLineText)
    }

    @Test
    fun `chapainawabganj displays as nawabganj on official numberplate`() {
        val config = PlateDesignConfig(
            plateType = PlateType.BIKE_FRONT,
            metroName = "চাঁপাইনবাবগঞ্জ",
            classLetter = "ল",
            seriesNumber = "১১",
            serialNumber = "১২৩৪"
        )
        assertEquals("নওয়াবগঞ্জ-ল-১১-১২৩৪", config.fullPlateNumberFormatted)
        assertEquals("নওয়াবগঞ্জ", config.displayMetroName)
    }

    @Test
    fun `bangla typeface loads cleanly from resources`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val typeface = com.example.engine.PlatePrintEngine.getBanglaTypeface(context)
        org.junit.Assert.assertNotNull(typeface)
    }

    @Test
    fun `brta logo drawable exists and decodes properly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val drawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_brta_logo)
        org.junit.Assert.assertNotNull(drawable)
    }

    @Test
    fun `room database inserts, queries, and deletes plates cleanly`() = kotlinx.coroutines.runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = androidx.room.Room.inMemoryDatabaseBuilder(
            context,
            com.example.data.AppDatabase::class.java
        ).build()

        val dao = db.plateDao()
        val config = PlateDesignConfig(
            plateType = PlateType.BIKE_FRONT,
            metroName = "ঢাকা মেট্রো",
            classLetter = "ল",
            seriesNumber = "২৫",
            serialNumber = "৯৯০০",
            bloodGroup = "O+",
            customTagline = "RIDER BD"
        )
        val entity = com.example.data.PlateEntity.fromConfig(config, "My Bike Plate")
        val id = dao.insertPlate(entity)
        assertTrue(id > 0)

        val retrieved = dao.getPlateById(id)
        org.junit.Assert.assertNotNull(retrieved)
        assertEquals("ঢাকা মেট্রো", retrieved?.metroName)
        assertEquals("ল", retrieved?.classLetter)
        assertEquals("২৫", retrieved?.seriesNumber)
        assertEquals("৯৯০০", retrieved?.serialNumber)
        assertEquals("O+", retrieved?.bloodGroup)
        assertEquals("RIDER BD", retrieved?.customTagline)

        val configRestored = retrieved!!.toConfig()
        assertEquals(PlateType.BIKE_FRONT, configRestored.plateType)
        assertEquals("ঢাকা মেট্রো-ল-২৫-৯৯০০", configRestored.fullPlateNumberFormatted)

        dao.deletePlate(retrieved)
        val deleted = dao.getPlateById(id)
        org.junit.Assert.assertNull(deleted)

        db.close()
    }

    @Test
    fun `viewmodel handles preset loading and plate dimension changes`() {
        val application = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.PlateEditorViewModel(application)

        viewModel.loadQuickPreset("DHAKA_BIKE_FRONT")
        assertEquals(PlateType.BIKE_FRONT, viewModel.config.value.plateType)
        assertEquals("ঢাকা মেট্রো", viewModel.config.value.metroName)
        assertEquals("ল", viewModel.config.value.classLetter)

        viewModel.setPlateType(PlateType.PRIVATE_CAR)
        assertEquals(PlateType.PRIVATE_CAR, viewModel.config.value.plateType)
        assertEquals(300f, viewModel.config.value.effectiveWidthMm, 0.1f)
        assertEquals(152f, viewModel.config.value.effectiveHeightMm, 0.1f)

        viewModel.setSeriesNumber("33")
        assertEquals("৩৩", viewModel.config.value.seriesNumber)

        viewModel.setSerialNumber("4455")
        assertEquals("৪৪৫৫", viewModel.config.value.serialNumber)

        viewModel.setBloodGroup("B+")
        assertEquals("B+", viewModel.config.value.bloodGroup)
    }

    @Test
    fun `a4 print page drawing renders cleanly on canvas for all plate types`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val types = listOf(
            PlateType.BIKE_FRONT,
            PlateType.BIKE_REAR,
            PlateType.SCOOTER,
            PlateType.PRIVATE_CAR,
            PlateType.CUSTOM
        )

        for (type in types) {
            val config = PlateDesignConfig(
                plateType = type,
                metroName = "ঢাকা মেট্রো",
                classLetter = "গ",
                seriesNumber = "১১",
                serialNumber = "২২৩৩"
            )
            val isLandscape = com.example.engine.PlatePrintEngine.isLandscapeRequired(config)
            val w = if (isLandscape) com.example.engine.PlatePrintEngine.A4_LONG_EDGE_PT else com.example.engine.PlatePrintEngine.A4_SHORT_EDGE_PT
            val h = if (isLandscape) com.example.engine.PlatePrintEngine.A4_SHORT_EDGE_PT else com.example.engine.PlatePrintEngine.A4_LONG_EDGE_PT

            val bitmap = android.graphics.Bitmap.createBitmap(w, h, android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)
            com.example.engine.PlatePrintEngine.drawA4PrintPage(canvas, config, w.toFloat(), h.toFloat(), null, context)
            assertTrue(bitmap.width > 0)
            assertTrue(bitmap.height > 0)
        }
    }

    @Test
    fun `high resolution bitmaps generate for all themes without crashing`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val themes = listOf(
            com.example.model.PlateTheme.STANDARD_WHITE,
            com.example.model.PlateTheme.COMMERCIAL_YELLOW,
            com.example.model.PlateTheme.ELECTRIC_GREEN,
            com.example.model.PlateTheme.STEALTH_BLACK,
            com.example.model.PlateTheme.CARBON_DARK
        )

        for (theme in themes) {
            val config = PlateDesignConfig(
                plateType = PlateType.BIKE_REAR,
                metroName = "চট্টগ্রাম মেট্রো",
                classLetter = "হ",
                seriesNumber = "১৬",
                serialNumber = "৭৮৯০",
                theme = theme
            )
            val bitmap = com.example.engine.PlatePrintEngine.createHighResBitmap(config, context)
            org.junit.Assert.assertNotNull(bitmap)
            assertTrue(bitmap.width > 500)
            assertTrue(bitmap.height > 300)

            val file = com.example.engine.PlatePrintEngine.saveBitmapToFile(context, bitmap, config)
            assertTrue(file.exists())
            assertTrue(file.length() > 1000)
        }
    }
}

