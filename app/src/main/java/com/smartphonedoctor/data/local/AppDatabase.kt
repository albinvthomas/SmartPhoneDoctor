package com.smartphonedoctor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartphonedoctor.data.local.dao.ScanResultDao
import com.smartphonedoctor.data.local.entity.ScanResultEntity

@Database(entities = [ScanResultEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanResultDao(): ScanResultDao
}
