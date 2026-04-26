package com.smartphonedoctor.domain

import com.smartphonedoctor.domain.model.*

class HealthScoreCalculator {

    fun calculate(
        batteryInfo: Result<BatteryInfo>,
        storageInfo: Result<DeviceStorageInfo>,
        activityInfo: Result<ActivityInfo>,
        issues: List<Issue>
    ): HealthScore {
        var batteryScore = 100
        var storageScore = 100
        var perfScore = 100

        // Base deductions from issues
        issues.forEach { issue ->
            when {
                issue.title.contains("Battery", ignoreCase = true) -> {
                    if (issue.severity == Severity.CRITICAL) batteryScore -= 40
                    else if (issue.severity == Severity.WARNING) batteryScore -= 20
                }
                issue.title.contains("Storage", ignoreCase = true) || issue.title.contains("cache", ignoreCase = true) -> {
                    if (issue.severity == Severity.CRITICAL) storageScore -= 40
                    else if (issue.severity == Severity.WARNING) storageScore -= 20
                    else if (issue.severity == Severity.INFO) storageScore -= 5
                }
                else -> {
                    // Assume performance issue for other drains (e.g. background drain)
                    if (issue.severity == Severity.WARNING && issue.title.contains("drains battery", ignoreCase = true)) {
                        perfScore -= 20
                        batteryScore -= 10
                    } else if (issue.severity == Severity.INFO) {
                        perfScore -= 5
                    }
                }
            }
        }

        // Additional deductions directly from data if available
        if (batteryInfo is Result.Success) {
            val battery = batteryInfo.data
            // e.g. BATTERY_HEALTH_DEAD or BATTERY_HEALTH_OVER_VOLTAGE etc.
            if (battery.healthStatus != android.os.BatteryManager.BATTERY_HEALTH_GOOD) {
                batteryScore -= 15
            }
        } else {
            batteryScore = 50 // Data missing/error
        }

        if (storageInfo is Result.Success) {
            val storage = storageInfo.data
            val freePercentage = if (storage.totalBytes > 0) {
                (storage.freeBytes.toDouble() / storage.totalBytes.toDouble()) * 100
            } else 100.0
            
            // Deduct slightly for being somewhat low, but not critical
            if (freePercentage < 20 && freePercentage >= 10) {
                storageScore -= 10
            }
        } else {
            storageScore = 50 // Data missing/error
        }

        if (activityInfo is Result.Success) {
            val activity = activityInfo.data
            if (activity.runningServices.size > 15) {
                perfScore -= 10
            }
        } else {
            perfScore = 50 // Data missing/error
        }

        batteryScore = batteryScore.coerceIn(0, 100)
        storageScore = storageScore.coerceIn(0, 100)
        perfScore = perfScore.coerceIn(0, 100)

        // score = (batteryScore * 0.35) + (storageScore * 0.35) + (perfScore * 0.30)
        val overallScore = ((batteryScore * 0.35) + (storageScore * 0.35) + (perfScore * 0.30)).toInt()

        return HealthScore(
            overallScore = overallScore,
            batteryScore = batteryScore,
            storageScore = storageScore,
            perfScore = perfScore
        )
    }
}
