package com.smartphonedoctor.domain.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}

data class BatteryInfo(
    val level: Int,
    val healthStatus: Int,
    val temperature: Float,
    val isCharging: Boolean
)

data class AppUsageStat(
    val packageName: String,
    val appName: String,
    val usageMinutes: Long
)

data class AppStorageStat(
    val packageName: String,
    val appSizeBytes: Long,
    val cacheBytes: Long
)

data class DeviceStorageInfo(
    val totalBytes: Long,
    val freeBytes: Long,
    val appStats: List<AppStorageStat>
)

data class ActivityInfo(
    val runningServices: List<String>,
    val memoryClass: Int
)

enum class Severity {
    CRITICAL, WARNING, INFO
}

data class Issue(
    val severity: Severity,
    val title: String,
    val description: String,
    val exactFix: String,
    val deepLinkAction: String? = null
)

data class HealthScore(
    val overallScore: Int,
    val batteryScore: Int,
    val storageScore: Int,
    val perfScore: Int
)

data class ScanResult(
    val issues: List<Issue>,
    val healthScore: HealthScore
)
