# PlateCraft BD 🇧🇩
### Bangladesh Vehicle Number Plate Designer & 1:1 True-Scale A4 Printing Studio

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**PlateCraft BD** is a modern, professional Android application built with **Jetpack Compose** and **Material 3** for designing, generating, and printing 1:1 physical-scale Bangladesh vehicle number plates following official **Bangladesh Road Transport Authority (BRTA)** specifications.

---

## ✨ Features

- **🇧🇩 100% BRTA Standard Compliant**:
  - Full support for all 64 districts & metro zones (*Dhaka Metro, Chattogram Metro, Sylhet Metro, Rajshahi Metro, Khulna Metro, Barishal Metro, Gazipur Metro, Rangpur Metro, etc.*).
  - Accurate district name formatting (including official BRTA rule: *চাঁপাইনবাবগঞ্জ* automatically formatted as **নওয়াবগঞ্জ**).
  - Complete vehicle class dropdown with Bengali descriptions and engine cc tiers (ক, খ, গ, ঘ, চ, ছ, জ, ঝ, ট, ঠ, ড, ঢ, থ, ল, হ, etc.).
  - Authentic Bengali typography using bundled high-legibility **Hind Siliguri** font.
  
- **📐 1:1 True-Scale Physical Printing Engine**:
  - **Bike Front Plate**: 200 mm × 100 mm (Landscape orientation on standard A4)
  - **Bike Rear Plate**: 135 mm × 155 mm (Portrait orientation on standard A4)
  - **Car Front & Rear Plates**: 520 mm × 110 mm (Standard BRTA long bar format)
  - Generates 300 DPI print-ready layouts with cutting border guides and mounting hole markings.
  - Direct system print spooler support (`PrintDocumentAdapter`) and high-resolution export.

- **🎨 Modern, Clean & Minimal UI**:
  - Sleek light theme inspired by Bangladesh vehicle utilities (Warm Amber `#EA580C`, Emerald Green `#006A4E`, and Crisp Card Surfaces).
  - Live interactive real-time plate preview.
  - Quick-pick vehicle categories (Motorcycle, Private Car, Microbus, Truck, CNG, etc.).

- **💾 Saved Designs & History**:
  - Device-local persistence using **Room Database**.
  - Edit, duplicate, re-print, or export past vehicle plate designs at any time.

---

## 🛠️ Architecture & Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) (SQLite + KSP)
- **Concurrency**: Kotlin Coroutines & `StateFlow`
- **Testing**: Local JVM Unit & Robolectric Component Tests

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio Ladybug (2024.2.1)** or newer
- **JDK 17** or newer
- **Android SDK Platform 34** (Target SDK: 34, Min SDK: 24)

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/YOUR_USERNAME/platecraft-bd.git

# Navigate to project folder
cd platecraft-bd

# Build debug APK
./gradlew assembleDebug

# Run unit and Robolectric tests
./gradlew testDebugUnitTest
```

---

## 📂 Project Structure

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/           # Room Database, DAO, and Entities
│   │   │   │   ├── model/          # BRTA Districts, Vehicle Classes, Plate Specs
│   │   │   │   ├── print/          # 1:1 True-Scale Print Engine & PDF Generator
│   │   │   │   ├── ui/             # Jetpack Compose Screens, ViewModels, Theme
│   │   │   │   └── MainActivity.kt # Main entry point with Edge-to-Edge support
│   │   │   ├── res/
│   │   │   │   ├── font/           # Bundled Bengali Font (Hind Siliguri)
│   │   │   │   ├── mipmap/         # Adaptive launcher icons
│   │   │   │   └── values/         # Strings, colors, and themes
│   │   │   └── AndroidManifest.xml
│   │   └── test/                   # Robolectric and JUnit unit tests
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml          # Gradle Version Catalog
├── settings.gradle.kts
└── README.md
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
