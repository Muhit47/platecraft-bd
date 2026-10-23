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
    fun `landscape required for bike front wide plate`() {
        val frontConfig = PlateDesignConfig(
            plateType = PlateType.BIKE_FRONT
        )
        assertTrue(com.example.engine.PlatePrintEngine.isLandscapeRequired(frontConfig))
    }

    @Test
    fun `splash screen strings load accurately from resources`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val title = context.getString(R.string.splash_title)
        val subtitle = context.getString(R.string.splash_subtitle)
        assertEquals("PlateCraft BD", title)
        assertEquals("বাংলাদেশ ভেহিক্যাল নাম্বার প্লেট স্টুডিও", subtitle)
    }
}

