package com.example.model

object BanglaConstants {

    val METRO_LIST = listOf(
        "ঢাকা মেট্রো",
        "চট্টগ্রাম মেট্রো",
        "সিলেট মেট্রো",
        "রাজশাহী মেট্রো",
        "খুলনা মেট্রো",
        "বরিশাল মেট্রো",
        "রংপুর মেট্রো",
        "ময়মনসিংহ মেট্রো",
        "ঢাকা",
        "চট্টগ্রাম",
        "গাজীপুর",
        "নারায়ণগঞ্জ",
        "কুমিল্লা",
        "বগুড়া",
        "যশোর",
        "সিলেট",
        "রাজশাহী",
        "খুলনা",
        "বরিশাল",
        "রংপুর",
        "ময়মনসিংহ",
        "দিনাজপুর",
        "পাবনা",
        "কুষ্টিয়া",
        "টাঙ্গাইল",
        "ফরিদপুর",
        "কক্সবাজার",
        "নোয়াখালী",
        "ব্রাহ্মণবাড়িয়া",
        "চাঁদপুর",
        "ফেনী",
        "লক্ষ্মীপুর",
        "জামালপুর",
        "নেত্রকোণা",
        "কিশোরগঞ্জ",
        "মানিকগঞ্জ",
        "মুন্সীগঞ্জ",
        "নরসিংদী",
        "রাজবাড়ী",
        "মাদারীপুর",
        "গোপালগঞ্জ",
        "শরীয়তপুর",
        "সিরাজগঞ্জ",
        "নওগাঁ",
        "নাটোর",
        "নওয়াবগঞ্জ",
        "জয়পুরহাট",
        "কুড়িগ্রাম",
        "গাইবান্ধা",
        "লালমনিরহাট",
        "নীলফামারী",
        "পঞ্চগড়",
        "ঠাকুরগাঁও",
        "বাগেরহাট",
        "সাতক্ষীরা",
        "ঝিনাইদহ",
        "মাগুরা",
        "নড়াইল",
        "চুয়াডাঙ্গা",
        "মেহেরপুর",
        "পটুয়াখালী",
        "ভোলা",
        "পিরোজপুর",
        "বরগুনা",
        "ঝালকাঠি",
        "হবিগঞ্জ",
        "মৌলভীবাজার",
        "সুনামগঞ্জ"
    )

    data class ClassLetterInfo(
        val letter: String,
        val english: String,
        val category: String
    )

    val VEHICLE_CLASSES = listOf(
        ClassLetterInfo("ল", "LA", "Motorcycle (50cc+) / মোটরসাইকেল"),
        ClassLetterInfo("হ", "HA", "Motorcycle (Up to 100cc) / ডেলিভারি ভ্যান"),
        ClassLetterInfo("ম", "MA", "Scooter & Motorcycle / স্কুটার"),
        ClassLetterInfo("ক", "KA", "Private Car (Up to 1000cc)"),
        ClassLetterInfo("খ", "KHA", "Private Car (1001cc to 1300cc)"),
        ClassLetterInfo("গ", "GA", "Private Car (1301cc to 1500cc)"),
        ClassLetterInfo("ঘ", "GHA", "Jeep / SUV / Microbus"),
        ClassLetterInfo("চ", "CHA", "Microbus / Maxi"),
        ClassLetterInfo("ছ", "CHHA", "Human Hauler / লেগুনা"),
        ClassLetterInfo("জ", "JA", "Minibus"),
        ClassLetterInfo("ঝ", "JHA", "Bus"),
        ClassLetterInfo("ট", "TA", "Large Truck / লরি"),
        ClassLetterInfo("ঠ", "THA", "Double Decker Bus"),
        ClassLetterInfo("ড", "DA", "Medium Truck"),
        ClassLetterInfo("ঢ", "DHA", "Covered Van"),
        ClassLetterInfo("ন", "NA", "Light Commercial Pickup"),
        ClassLetterInfo("প", "PA", "Taxi Cab"),
        ClassLetterInfo("ব", "BA", "Large Omnibus"),
        ClassLetterInfo("ভ", "BHA", "Ambulance / Emergency"),
        ClassLetterInfo("থ", "THHA", "Auto Rickshaw (CNG)")
    )

    val BLOOD_GROUPS = listOf(
        "None",
        "A+",
        "A-",
        "B+",
        "B-",
        "O+",
        "O-",
        "AB+",
        "AB-"
    )

    private val ENG_TO_BN_DIGIT_MAP = mapOf(
        '0' to '০',
        '1' to '১',
        '2' to '২',
        '3' to '৩',
        '4' to '৪',
        '5' to '৫',
        '6' to '৬',
        '7' to '৭',
        '8' to '৮',
        '9' to '৯'
    )

    private val BN_TO_ENG_DIGIT_MAP = ENG_TO_BN_DIGIT_MAP.entries.associate { (k, v) -> v to k }

    fun toBanglaDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            sb.append(ENG_TO_BN_DIGIT_MAP[ch] ?: ch)
        }
        return sb.toString()
    }

    fun toEnglishDigits(input: String): String {
        val sb = StringBuilder()
        for (ch in input) {
            sb.append(BN_TO_ENG_DIGIT_MAP[ch] ?: ch)
        }
        return sb.toString()
    }

    fun filterOnlyDigits(input: String, maxLen: Int): String {
        val converted = toBanglaDigits(input)
        val validBanglaDigits = "০১২৩৪৫৬৭৮৯"
        val filtered = converted.filter { it in validBanglaDigits }
        return if (filtered.length > maxLen) filtered.take(maxLen) else filtered
    }

    fun normalizeMetroName(name: String): String {
        return when (name.trim()) {
            "চাঁপাইনবাবগঞ্জ", "চাঁপাই নবাবগঞ্জ", "নওয়াবগঞ্জ" -> "নওয়াবগঞ্জ"
            else -> name.trim()
        }
    }
}
