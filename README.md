<div align="center">

# 📱 SmartPhoneDoctor

**Scan your phone. Find problems. Get exact fixes.**

[![Download App](https://img.shields.io/badge/Download_APK_v1.3.0-2EA043?style=for-the-badge&logo=android&logoColor=white)](https://github.com/albinvthomas/SmartPhoneDoctor/releases/download/v1.3.0/app-debug.apk)

![Android Version](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=flat-square&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin)
![Material Design 3](https://img.shields.io/badge/Material_3-F8EFE7?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)

</div>

## 📥 Download and Install

> **⚠️ Note:** You may need to enable **"Install Unknown Apps"** in your device settings to install the APK directly from GitHub.

1. Click the green **Download APK** button above.
2. Open the downloaded `app-debug.apk` file on your Android device.
3. Tap **Install** and open the app.
4. Grant the **Usage Access** permission when prompted (required for analyzing app battery drain).
5. Tap **Scan My Phone** to get your health score!

## ✨ Features

| | Feature | Description |
|---|---|---|
| 🩺 | **One-tap Smart Scan** | Diagnoses your entire phone in seconds with a single tap. |
| 🔋 | **Battery Health Analyzer** | Checks temperature, level, and background drain issues. |
| 💾 | **Storage Issue Detector** | Identifies large app caches and low free space. |
| 📱 | **App Usage Monitor** | Pinpoints which apps are consuming excessive screen time. |
| 🔧 | **Exact Fix Suggestions** | Provides one-tap deep links straight to the necessary device settings. |
| 💯 | **Health Score** | Rates your device health out of 100 based on active issues. |
| 📈 | **Weekly History Trends** | Saves past scans to track your phone's performance over time. |

## ⚙️ How It Works

**Tap Scan** ➔ **Analyze Device** ➔ **See Score + Fixes**

## 🛠 Tech Stack

| Technology | Purpose |
|---|---|
| **Kotlin** | Primary programming language |
| **Jetpack Compose + Material 3** | Modern, declarative, dynamic UI toolkit |
| **MVVM + Clean Architecture** | Scalable app structure and logic separation |
| **Hilt** | Robust dependency injection framework |
| **Room Database** | Local data persistence for scan history |
| **WorkManager** | Reliable background task scheduling |
| **Android System APIs** | Hardware status and usage stats monitoring |

## 🔒 Permissions

| Permission | Purpose |
|---|---|
| **Usage Stats** | Analyzes foreground screen time to find battery-draining apps. |
| **Battery** | Reads battery health, temperature, and charging status. |
| **Storage** | Calculates total free space and individual app cache sizes. |

> **Your Privacy is Respected:** All data stays 100% on your device. Nothing is ever uploaded or shared.

## 💻 Build From Source

```bash
git clone https://github.com/albinvthomas/SmartPhoneDoctor.git
```

1. Open the project in **Android Studio**.
2. Let Gradle sync the project and download all dependencies.
3. Click the green **Run** button to build and deploy to your emulator or physical device.

## 👨‍💻 Developer

**Albin V Thomas**  
GitHub: [albinvthomas](https://github.com/albinvthomas)

## 📄 License

This project is licensed under the MIT License.
