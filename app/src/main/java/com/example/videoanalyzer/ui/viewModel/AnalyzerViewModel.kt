package com.example.videoanalyzer.ui.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.ui.state.AnalyzerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AnalyzerViewModel:  ViewModel() {
    private val _uiState = MutableStateFlow(AnalyzerUiState())
    // 提供外部访问的state
    val uiState: StateFlow<AnalyzerUiState> = _uiState.asStateFlow()

    // 用于外部调用的更新状态方法
    // 更新视频Uri
    fun updateVideoUri(
        videoUri: Uri
    ) {
        _uiState.update { currentUpdate ->
            currentUpdate.copy(
                videoUri = videoUri
            )
        }
    }

    // 更新应用状态
    fun updateAppStatus(
        appStatus: AppStatus
    ) {
        _uiState.update { currentUpdate ->
            currentUpdate.copy(
                appStatus = appStatus
            )
        }
    }
}
