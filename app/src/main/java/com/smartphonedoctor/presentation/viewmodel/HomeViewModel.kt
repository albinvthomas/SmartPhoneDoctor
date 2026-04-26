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

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    data class Success(val result: ScanResult) : ScanState()
    data class Error(val message: String) : ScanState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataCollector: DataCollector,
    private val phoneHealthAnalyzer: PhoneHealthAnalyzer
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanState>(ScanState.Idle)
    val uiState: StateFlow<ScanState> = _uiState.asStateFlow()

    fun startScan() {
        _uiState.value = ScanState.Scanning
        viewModelScope.launch {
            try {
                val batteryInfo = dataCollector.collectBatteryInfo().let {
                    if (it is com.smartphonedoctor.domain.model.Result.Success) it.data
                    else throw Exception("Battery info collection failed")
                }
                
                val usageStats = dataCollector.collectUsageStats().let {
                    if (it is com.smartphonedoctor.domain.model.Result.Success) it.data
                    else emptyList()
                }
                
                val storageInfo = dataCollector.collectStorageStats().let {
                    if (it is com.smartphonedoctor.domain.model.Result.Success) it.data
                    else throw Exception("Storage info collection failed")
                }
                
                val activityInfo = dataCollector.collectActivityInfo().let {
                    if (it is com.smartphonedoctor.domain.model.Result.Success) it.data
                    else throw Exception("Activity info collection failed")
                }

                val scanResult = phoneHealthAnalyzer.analyze(
                    batteryInfo = batteryInfo,
                    usageStats = usageStats,
                    storageInfo = storageInfo,
                    activityInfo = activityInfo
                )
                
                _uiState.value = ScanState.Success(scanResult)
            } catch (e: Exception) {
                _uiState.value = ScanState.Error(e.message ?: "Scan failed")
            }
        }
    }
}
