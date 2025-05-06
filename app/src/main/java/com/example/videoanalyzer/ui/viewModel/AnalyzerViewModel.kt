package com.example.videoanalyzer.ui.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.service.ModelVlService
import com.example.videoanalyzer.ui.state.AnalyzerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import androidx.core.graphics.scale

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
    fun analyzeVideo(context: Context) {
        // TODO 上传至api处理视频分析结果
        viewModelScope.launch {
            // test(uiState.value.frameTotal)
            analyze(context)
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

    /**
     * 分析视频并提取关键帧文本信息
     */
    private suspend fun analyze(
        context: Context
    ) {
        // 如果 Uri 为空则不处理
        val videoUri = uiState.value.videoUri.takeIf { it != Uri.EMPTY }
            ?: return

        // 分析结果列表
        val textList = ArrayList<String>()

        // 时间间隔
        val intervalMs = uiState.value.frameInterval

        // 切换到 IO 线程进行耗时操作
        withContext(Dispatchers.IO) {
            val retriever = MediaMetadataRetriever()
            try {
                // 根据 Uri 设置数据源
                retriever.setDataSource(context, videoUri)

                val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val durationMs = durationStr?.toLongOrNull() ?: 0L

                // 如果总帧数为0，则赋值为1
                var frameTotal = (durationMs / intervalMs).toInt()
                frameTotal = if (frameTotal == 0) 1 else frameTotal
                // 更新帧总数
                updateFrameTotal(frameTotal)

                // 每隔指定秒提取一帧（可配置）
                for (i in 1 until frameTotal+1) {
                    // 如果frameTotal为1， 则提取首帧即可
                    val bitmap: Bitmap? = if (frameTotal == 1) {
                        retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    } else {
                        val timeMs = (i * intervalMs)
                        retriever.getFrameAtTime(timeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                    }

                    if (bitmap != null) {
                        // 描述图像，输出文本
                        val recognizedText = async {
                            recognizeTextFromBitmap(bitmap)
                        }
                        textList.add(recognizedText.await())
                    }

                    updateCurrentFrameNumber(i + 0)
                    //delay(50L) // 模拟耗时
                }

                // 更新分析结果
                updateAnalyzeResult(textList)
                // 更新app状态
                updateAppStatus(AppStatus.ANALYZED)
            } catch (e: Exception) {
                e.printStackTrace()
                updateAppStatus(AppStatus.ERROR)
            } finally {
                retriever.release()
            }
        }
    }

    /**
     * 图像识别方法
     *
     */
    private suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String {
        // 缩放图像到较小尺寸（512x512）
        val scaledBitmap = bitmap.scale(512, 512)


        // 将 Bitmap 转换为 JPG 格式的字节数组
        val byteArrayOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        // 将字节数组进行 Base64 编码
        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)

        // 将 Base64 编码的图片数据传递给 API
        return ModelVlService.chatWithApi(base64Image)
    }
}
