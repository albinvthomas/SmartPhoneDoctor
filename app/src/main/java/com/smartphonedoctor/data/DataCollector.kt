package com.smartphonedoctor.data

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.util.Log
import com.smartphonedoctor.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class DataCollector(private val context: Context) {

    suspend fun collectBatteryInfo(): Result<BatteryInfo> = withContext(Dispatchers.IO) {
        try {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
                context.registerReceiver(null, ifilter)
            }
            
            val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale) else -1
            
            val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL
                    
            val health: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
            val temp: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
            
            Result.Success(
                BatteryInfo(
                    level = batteryPct,
                    healthStatus = health,
                    temperature = temp / 10f, // Temperature is returned in tenths of a degree Centigrade
                    isCharging = isCharging
                )
            )
        } catch (e: Exception) {
            Log.e("SmartPhoneDoctor", "BatteryInfo failed: ${e.message}", e)
            Result.Success(
                BatteryInfo(
                    level = 100,
                    healthStatus = BatteryManager.BATTERY_HEALTH_GOOD,
                    temperature = 25f,
                    isCharging = false
                )
            )
        }
    }

    suspend fun collectUsageStats(): Result<List<AppUsageStat>> = withContext(Dispatchers.IO) {
        if (!com.smartphonedoctor.util.PermissionHelper.hasUsageStatsPermission(context)) {
            Log.w("SmartPhoneDoctor", "Usage stats permission not granted, skipping")
            return@withContext Result.Success(emptyList())
        }
        
        try {
            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val calendar = Calendar.getInstance()
            val endTime = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startTime = calendar.timeInMillis
            
            val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
            val pm = context.packageManager
            
            val appUsageList = usageStats.mapNotNull { stat ->
                val totalTimeInForeground = stat.totalTimeInForeground
                if (totalTimeInForeground > 0) {
                    val appName = try {
                        val appInfo = pm.getApplicationInfo(stat.packageName, 0)
                        pm.getApplicationLabel(appInfo).toString()
                    } catch (e: PackageManager.NameNotFoundException) {
                        stat.packageName
                    }
                    
                    AppUsageStat(
                        packageName = stat.packageName,
                        appName = appName,
                        usageMinutes = totalTimeInForeground / (1000 * 60)
                    )
                } else null
            }.groupBy { it.packageName }.map { (packageName, stats) ->
                val totalMinutes = stats.sumOf { it.usageMinutes }
                val appName = stats.first().appName
                AppUsageStat(packageName, appName, totalMinutes)
            }.sortedByDescending { it.usageMinutes }
            
            Result.Success(appUsageList)
        } catch (e: SecurityException) {
            Log.e("SmartPhoneDoctor", "AppUsage SecurityException: ${e.message}", e)
            Result.Success(emptyList())
        } catch (e: Exception) {
            Log.e("SmartPhoneDoctor", "AppUsage failed: ${e.message}", e)
            Result.Success(emptyList())
        }
    }

    suspend fun collectStorageStats(): Result<DeviceStorageInfo> = withContext(Dispatchers.IO) {
        try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val pm = context.packageManager
            
            val packages = pm.getInstalledApplications(0)
            val appStats = mutableListOf<AppStorageStat>()
            
            for (appInfo in packages) {
                try {
                    val stats = storageStatsManager.queryStatsForUid(StorageManager.UUID_DEFAULT, appInfo.uid)
                    appStats.add(
                        AppStorageStat(
                            packageName = appInfo.packageName,
                            appSizeBytes = stats.appBytes + stats.dataBytes,
                            cacheBytes = stats.cacheBytes
                        )
                    )
                } catch (e: Exception) {
                    Log.e("SmartPhoneDoctor", "Storage stats for ${appInfo.packageName} failed: ${e.message}", e)
                    // Ignore if stats cannot be accessed for this specific package
                }
            }
            
            val dataDir = Environment.getDataDirectory()
            val statFs = StatFs(dataDir.path)
            val blockSize = statFs.blockSizeLong
            val totalBlocks = statFs.blockCountLong
            val availableBlocks = statFs.availableBlocksLong
            
            val totalBytes = totalBlocks * blockSize
            val freeBytes = availableBlocks * blockSize
            
            Result.Success(
                DeviceStorageInfo(
                    totalBytes = totalBytes,
                    freeBytes = freeBytes,
                    appStats = appStats
                )
            )
        } catch (e: SecurityException) {
            Log.e("SmartPhoneDoctor", "Storage SecurityException: ${e.message}", e)
            Result.Success(DeviceStorageInfo(totalBytes = 1L, freeBytes = 1L, appStats = emptyList()))
        } catch (e: Exception) {
            Log.e("SmartPhoneDoctor", "Storage failed: ${e.message}", e)
            Result.Success(DeviceStorageInfo(totalBytes = 1L, freeBytes = 1L, appStats = emptyList()))
        }
    }

    suspend fun collectActivityInfo(): Result<ActivityInfo> = withContext(Dispatchers.IO) {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            
            @Suppress("DEPRECATION")
            val runningServices = activityManager.getRunningServices(Int.MAX_VALUE).map { 
                it.service.className 
            }
            
            val memoryClass = activityManager.memoryClass
            
            Result.Success(
                ActivityInfo(
                    runningServices = runningServices,
                    memoryClass = memoryClass
                )
            )
        } catch (e: Exception) {
            Log.e("SmartPhoneDoctor", "ActivityInfo failed: ${e.message}", e)
            Result.Success(ActivityInfo(emptyList(), 0))
        }
    }
}
