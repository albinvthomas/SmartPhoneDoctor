package com.smartphonedoctor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartphonedoctor.data.local.dao.ScanResultDao
import com.smartphonedoctor.data.local.entity.ScanResultEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val scanResultDao: ScanResultDao
) : ViewModel() {

    val history: StateFlow<List<ScanResultEntity>> = scanResultDao.getLast7Results()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
