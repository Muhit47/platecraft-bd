package com.example.model

import androidx.compose.ui.graphics.Color

enum class PlateTheme(
    val title: String,
    val bengaliTitle: String,
    val bgColor: Color,
    val textColor: Color,
    val borderColor: Color,
    val isRetroReflective: Boolean
) {
    STANDARD_WHITE(
        title = "Official White",
        bengaliTitle = "অফিসিয়াল সাদা",
        bgColor = Color(0xFFFBFBFB),
        textColor = Color(0xFF111827),
        borderColor = Color(0xFF111827),
        isRetroReflective = true
    ),
    COMMERCIAL_YELLOW(
        title = "Commercial Yellow",
        bengaliTitle = "বাণিজ্যিক হলুদ",
        bgColor = Color(0xFFFFD500),
        textColor = Color(0xFF111827),
        borderColor = Color(0xFF111827),
        isRetroReflective = true
    ),
    ELECTRIC_GREEN(
        title = "Electric Vehicle Green",
        bengaliTitle = "ইভি গ্রিন",
        bgColor = Color(0xFF00875A),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFFFFFFFF),
        isRetroReflective = true
    ),
    STEALTH_BLACK(
        title = "Stealth Black",
        bengaliTitle = "স্টেল্থ ব্ল্যাক",
        bgColor = Color(0xFF18181B),
        textColor = Color(0xFFF4F4F5),
        borderColor = Color(0xFF3F3F46),
        isRetroReflective = false
    ),
    CARBON_DARK(
        title = "Carbon Fiber",
        bengaliTitle = "কার্বন ফাইবার",
        bgColor = Color(0xFF27272A),
        textColor = Color(0xFFFAFAFA),
        borderColor = Color(0xFF71717A),
        isRetroReflective = false
    )
}
