package com.smartphonedoctor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val healthScore: Int,
    val issueCount: Int,
    val batteryScore: Int,
    val storageScore: Int,
    val perfScore: Int
)
