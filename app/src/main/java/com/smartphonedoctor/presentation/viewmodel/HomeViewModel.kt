package com.smartphonedoctor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartphonedoctor.data.DataCollector
import com.smartphonedoctor.domain.PhoneHealthAnalyzer
import com.smartphonedoctor.domain.model.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.smartphonedoctor.data.local.dao.ScanResultDao
import com.smartphonedoctor.data.local.entity.ScanResultEntity
import android.util.Log

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    data class Success(val result: ScanResult) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataCollector: DataCollector,
    private val phoneHealthAnalyzer: PhoneHealthAnalyzer,
    private val scanResultDao: ScanResultDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanState>(ScanState.Idle)
    val uiState: StateFlow<ScanState> = _uiState.asStateFlow()

    fun startScan() {
        _uiState.value = ScanState.Scanning
        viewModelScope.launch {
            try {
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
                
                scanResultDao.insert(
                    ScanResultEntity(
                        timestamp = System.currentTimeMillis(),
                        healthScore = scanResult.healthScore.overallScore,
                        issueCount = scanResult.issues.size,
                        batteryScore = scanResult.healthScore.batteryScore,
                        storageScore = scanResult.healthScore.storageScore,
                        perfScore = scanResult.healthScore.perfScore
                    )
                )
                
                _uiState.value = ScanState.Success(scanResult)
            } catch (e: SecurityException) {
                Log.e("SmartPhoneDoctor", "PERMISSION DENIED: ${e.message}", e)
                _uiState.value = ScanState.Error("Permission denied: ${e.message}")
            } catch (e: Exception) {
                Log.e("SmartPhoneDoctor", "SCAN CRASH: ${e.message}", e)
                _uiState.value = ScanState.Error(e.message ?: "Unknown crash")
            }
        }
    }
}
