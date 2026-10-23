package com.example.model

enum class PlateType(
    val title: String,
    val bengaliTitle: String,
    val widthMm: Float,
    val heightMm: Float,
    val description: String
) {
    BIKE_FRONT(
        title = "Bike Front",
        bengaliTitle = "বাইক ফ্রন্ট",
        widthMm = 285f,
        heightMm = 50f,
        description = "Standard front strap plate required for motorcycles (285 × 50 mm)"
    ),
    BIKE_REAR(
        title = "Bike Rear (RFID)",
        bengaliTitle = "বাইক রিয়ার",
        widthMm = 205f,
        heightMm = 152f,
        description = "Official BRTA retro-reflective RFID rear plate (205 × 152 mm)"
    ),
    SCOOTER(
        title = "Scooter",
        bengaliTitle = "স্কুটার",
        widthMm = 200f,
        heightMm = 100f,
        description = "BRTA standard square scooter plate (200 × 100 mm)"
    ),
    PRIVATE_CAR(
        title = "Private Car",
        bengaliTitle = "প্রাইভেট কার",
        widthMm = 300f,
        heightMm = 152f,
        description = "Standard passenger car & microbus plate (300 × 152 mm)"
    ),
    CUSTOM(
        title = "Custom Size",
        bengaliTitle = "কাস্টম সাইজ",
        widthMm = 250f,
        heightMm = 100f,
        description = "User-defined dimensions for custom brackets and showplates"
    )
}
