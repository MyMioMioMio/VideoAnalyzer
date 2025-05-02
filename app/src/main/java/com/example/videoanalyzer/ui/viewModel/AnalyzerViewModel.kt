package com.example.videoanalyzer.ui.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.ui.state.AnalyzerUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    // 更新当前处理帧的编号
    fun updateCurrentFrameNumber(
        currentFrameNumber: Int
    ) {
        _uiState.update { currentUpdate ->
            currentUpdate.copy(
                currentFrameNumber = currentFrameNumber
            )
        }
    }

    // 更新视频分析结果
    fun updateAnalyzeResult(
        textList: ArrayList<String>
    ) {
        _uiState.update { currentUpdate ->
            currentUpdate.copy(
                textList = textList
            )
        }
    }

    // 修改帧总数
    fun updateFrameTotal(
        frameTotal: Int
    ) {
        _uiState.update { currentUpdate ->
            currentUpdate.copy(
                frameTotal = frameTotal
            )
        }
    }

    // TODO 异步处理视频（测试）
    fun analyzeVideo() {
        // TODO 处理视频获取关键帧集合 ,需要优化协程上的处理
        updateFrameTotal(50)

        // TODO 上传至api处理视频分析结果
        viewModelScope.launch {
            test(uiState.value.frameTotal)
        }
    }

    suspend fun test(
        frameTotal: Int
    ) {
        val textList = ArrayList<String>()
        for (i in 0 until frameTotal) {
            delay(50L)
            textList.add("test....")
            updateCurrentFrameNumber(i + 1)
        }
        updateAnalyzeResult(textList)
        // 更新应用状态为分析完成
        updateAppStatus(AppStatus.ANALYZED)
    }
}
