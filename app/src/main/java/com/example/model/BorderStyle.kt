package com.example.model

enum class BorderStyle(
    val title: String,
    val bengaliTitle: String
) {
    STANDARD_EMBOSSED("Embossed Frame", "এমবসড ফ্রেম"),
    DOUBLE_BORDER("Double Border", "ডাবল বর্ডার"),
    THIN_BORDER("Thin Outline", "পাতলা বর্ডার"),
    NO_BORDER("Frameless", "বর্ডারহীন")
}
