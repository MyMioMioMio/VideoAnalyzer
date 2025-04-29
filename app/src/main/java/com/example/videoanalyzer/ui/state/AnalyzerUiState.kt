package com.example.videoanalyzer.ui.state

import android.net.Uri

// 视频分析器UI状态
data class AnalyzerUiState(
    // 视频链接
    val videoUri: Uri = Uri.EMPTY
)
