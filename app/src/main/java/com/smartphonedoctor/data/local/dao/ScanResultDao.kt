package com.smartphonedoctor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.smartphonedoctor.data.local.entity.ScanResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scanResult: ScanResultEntity)

    @Query("SELECT * FROM scan_results ORDER BY timestamp DESC LIMIT 7")
    fun getLast7Results(): Flow<List<ScanResultEntity>>
}
