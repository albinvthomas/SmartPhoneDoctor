package com.smartphonedoctor.domain.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.smartphonedoctor.MainActivity
import com.smartphonedoctor.R
import com.smartphonedoctor.data.DataCollector
import com.smartphonedoctor.data.local.dao.ScanResultDao
import com.smartphonedoctor.data.local.entity.ScanResultEntity
import com.smartphonedoctor.domain.PhoneHealthAnalyzer
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class WeeklyHealthScanWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dataCollector: DataCollector,
    private val phoneHealthAnalyzer: PhoneHealthAnalyzer,
    private val scanResultDao: ScanResultDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val batteryInfo = dataCollector.collectBatteryInfo()
            val usageStats = dataCollector.collectUsageStats()
            val storageInfo = dataCollector.collectStorageStats()
            val activityInfo = dataCollector.collectActivityInfo()

            val scanResult = phoneHealthAnalyzer.analyze(
                batteryInfo = batteryInfo,
                usageStats = usageStats,
                storageInfo = storageInfo,
                activityInfo = activityInfo
            )

            val healthScore = scanResult.healthScore
            val entity = ScanResultEntity(
                timestamp = System.currentTimeMillis(),
                healthScore = healthScore.overallScore,
                issueCount = scanResult.issues.size,
                batteryScore = healthScore.batteryScore,
                storageScore = healthScore.storageScore,
                perfScore = healthScore.perfScore
            )

            scanResultDao.insert(entity)
            showNotification(healthScore.overallScore)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(score: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "health_scan_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Health Scan Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Weekly Scan Complete")
            .setContentText("Your phone health score is $score/100 — tap to see issues.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(1, notification)
    }
}
