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

        // 1. Battery < 20% AND temperature > 40°C -> CRITICAL
        if (batteryInfo is Result.Success) {
            val battery = batteryInfo.data
            if (battery.level in 0..19 && battery.temperature > 40f) {
                issues.add(
                    Issue(
                        severity = Severity.CRITICAL,
                        title = "Battery critically hot",
                        description = "Battery is at ${battery.level}% and temperature is ${battery.temperature}°C.",
                        exactFix = "Close background apps and let the phone cool down.",
                        deepLinkAction = Settings.ACTION_BATTERY_SAVER_SETTINGS
                    )
                )
            }
        }

        val appsUsed = if (usageStats is Result.Success) usageStats.data else emptyList()
        val activities = if (activityInfo is Result.Success) activityInfo.data else null

        // 2. usageMinutes > 180/day AND running in background -> WARNING
        if (activities != null) {
            val runningClassNames = activities.runningServices
            appsUsed.forEach { app ->
                val dailyUsageMins = app.usageMinutes / 7 // Based on DataCollector 7-day query
                val isRunning = runningClassNames.any { it.startsWith(app.packageName) }
                
                if (dailyUsageMins > 180 && isRunning) {
                    issues.add(
                        Issue(
                            severity = Severity.WARNING,
                            title = "${app.appName} drains battery in background",
                            description = "App is heavily used and is currently running in the background.",
                            exactFix = "Restrict background activity.",
                            deepLinkAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        )
                    )
                }
            }
        }

        if (storageInfo is Result.Success) {
            val storage = storageInfo.data
            
            // 4. Total free storage < 10% of total -> CRITICAL
            val freePercentage = if (storage.totalBytes > 0) {
                (storage.freeBytes.toDouble() / storage.totalBytes.toDouble()) * 100
            } else 100.0

            if (freePercentage < 10.0) {
                issues.add(
                    Issue(
                        severity = Severity.CRITICAL,
                        title = "Storage almost full",
                        description = "Less than 10% of total storage is free.",
                        exactFix = "Uninstall unused apps.",
                        deepLinkAction = Settings.ACTION_INTERNAL_STORAGE_SETTINGS
                    )
                )
            }

            val usedPackageNames = appsUsed.map { it.packageName }.toSet()

            storage.appStats.forEach { appStat ->
                // 3. Any app with cacheBytes > 200MB -> WARNING
                val cacheMB = appStat.cacheBytes / (1024 * 1024)
                if (cacheMB > 200) {
                    val appName = appsUsed.find { it.packageName == appStat.packageName }?.appName ?: appStat.packageName
                    issues.add(
                        Issue(
                            severity = Severity.WARNING,
                            title = "$appName cache is ${cacheMB}MB",
                            description = "App has accumulated a large amount of cache data.",
                            exactFix = "Clear cache in app settings.",
                            deepLinkAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        )
                    )
                }

                // 5. Apps not opened in 30 days with size > 50MB -> INFO
                // Using "not in 7-day usageStats" as an approximation since DataCollector only has 7 days.
                if (!usedPackageNames.contains(appStat.packageName)) {
                    val sizeMB = appStat.appSizeBytes / (1024 * 1024)
                    if (sizeMB > 50) {
                        val appName = appStat.packageName // Use package name if app name is unknown
                        issues.add(
                            Issue(
                                severity = Severity.INFO,
                                title = "You haven't used $appName in 30 days",
                                description = "App uses ${sizeMB}MB but hasn't been opened recently.",
                                exactFix = "Uninstall to free ${sizeMB}MB.",
                                deepLinkAction = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            )
                        )
                    }
                }
            }
        }

        val healthScore = healthScoreCalculator.calculate(batteryInfo, storageInfo, activityInfo, issues)

        return ScanResult(issues, healthScore)
    }
}
