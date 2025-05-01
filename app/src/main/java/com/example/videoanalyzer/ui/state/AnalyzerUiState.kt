package com.example.videoanalyzer.ui.state

import android.net.Uri
import com.example.videoanalyzer.enums.AppStatus

// 视频分析器UI状态
data class AnalyzerUiState(
    // 视频链接
    val videoUri: Uri = Uri.EMPTY,
    // 应用状态
    val appStatus: AppStatus = AppStatus.WAIT_FOR_IMPORT_VIDEO,
)
