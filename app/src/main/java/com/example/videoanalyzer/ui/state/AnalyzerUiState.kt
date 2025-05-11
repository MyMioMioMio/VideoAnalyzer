package com.example.videoanalyzer.ui.state

import android.net.Uri
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.models.HistoryList

// 视频分析器UI状态
data class AnalyzerUiState(
    // 视频链接
    val videoUri: Uri = Uri.EMPTY,
    // 应用状态
    val appStatus: AppStatus = AppStatus.WAIT_FOR_IMPORT_VIDEO,
    // 识别结果
    val textList: List<String> = ArrayList<String>(),
    // 当前处理帧的编号
    val currentFrameNumber: Int = 0,
    // 视频帧总数
    val frameTotal: Int = 1,
    // 获取帧的时间间隔
    val frameInterval: Long = 10000L,
    // 临时历史记录列表
    val tempHistoryList: List<HistoryList> = ArrayList<HistoryList>()
)
