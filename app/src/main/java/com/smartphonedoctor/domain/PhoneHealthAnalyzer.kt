package com.smartphonedoctor.domain

import android.provider.Settings
import com.smartphonedoctor.domain.model.*

class PhoneHealthAnalyzer(
    private val healthScoreCalculator: HealthScoreCalculator
) {
    fun analyze(
        batteryInfo: Result<BatteryInfo>,
        usageStats: Result<List<AppUsageStat>>,
        storageInfo: Result<DeviceStorageInfo>,
        activityInfo: Result<ActivityInfo>
    ): ScanResult {
        val issues = mutableListOf<Issue>()

        // 1. REAL BATTERY check: temp > 37f OR level < 20
        if (batteryInfo is Result.Success) {
            val battery = batteryInfo.data
            if (battery.temperature > 37f) {
                issues.add(
                    Issue(
                        severity = Severity.WARNING,
                        title = "Battery is running hot",
                        description = "Temperature is ${battery.temperature}°C.",
                        exactFix = "Close background apps and let the phone cool down.",
                        deepLinkAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
                    )
                )
            }
            if (battery.level in 0..19) {
                issues.add(
                    Issue(
                        severity = Severity.WARNING,
                        title = "Battery level is low",
                        description = "Battery is at ${battery.level}%.",
                        exactFix = "Plug in your charger.",
                        deepLinkAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
                    )
                )
            }
        }

        val appsUsed = if (usageStats is Result.Success) usageStats.data else emptyList()

        // 2. REAL APP USAGE check: usage > 3 hours
        appsUsed.forEach { app ->
            if (app.usageMinutes > 180) { // 180 mins = 3 hours
                val hours = app.usageMinutes / 60
                val mins = app.usageMinutes % 60
                val timeString = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
                issues.add(
                    Issue(
                        severity = Severity.INFO,
                        title = "${app.appName} high usage",
                        description = "You've used this app for $timeString.",
                        exactFix = "Consider restricting background activity if battery drains fast.",
                        deepLinkAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    )
                )
            }
        }

        if (storageInfo is Result.Success) {
            val storage = storageInfo.data
            
            // 3. REAL STORAGE check: freePercent < 15f
            val freePercentage = if (storage.totalBytes > 0) {
                (storage.freeBytes.toFloat() / storage.totalBytes.toFloat()) * 100
            } else 100f

            if (freePercentage < 15f) {
                issues.add(
                    Issue(
                        severity = Severity.CRITICAL,
                        title = "Storage almost full",
                        description = "Only %.1f%% (${formatSize(storage.freeBytes)}) of ${formatSize(storage.totalBytes)} is free.".format(freePercentage),
                        exactFix = "Uninstall unused apps or clear large files.",
                        deepLinkAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
                    )
                )
            }

            storage.appStats.forEach { appStat ->
                // 4. REAL CACHE check: cacheBytes > 100MB
                if (appStat.cacheBytes > 100L * 1024 * 1024) {
                    val appName = appsUsed.find { it.packageName == appStat.packageName }?.appName ?: appStat.packageName
                    issues.add(
                        Issue(
                            severity = Severity.WARNING,
                            title = "$appName cache is large",
                            description = "App has accumulated ${formatSize(appStat.cacheBytes)} of cache data.",
                            exactFix = "Clear cache in app settings.",
                            deepLinkAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        )
                    )
                }
            }
        }

        val healthScore = healthScoreCalculator.calculate(batteryInfo, storageInfo, activityInfo, issues)

        return ScanResult(issues, healthScore)
    }

    private fun formatSize(bytes: Long): String = when {
        bytes > 1_000_000_000 -> "%.1f GB".format(bytes / 1e9)
        bytes > 1_000_000 -> "%.0f MB".format(bytes / 1e6)
        else -> "%.0f KB".format(bytes / 1e3)
    }
}
