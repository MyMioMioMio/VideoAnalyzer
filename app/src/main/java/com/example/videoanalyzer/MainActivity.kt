package com.example.videoanalyzer

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.service.SparkService
import com.example.videoanalyzer.ui.VideoAnalyzerApp
import com.example.videoanalyzer.ui.theme.VideoAnalyzerTheme
import com.example.videoanalyzer.ui.viewModel.AnalyzerViewModel
import com.example.videoanalyzer.utils.Tts

/**
 * 给盲人使用的视频分析应用
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoAnalyzerTheme {
                val viewModel = viewModel<AnalyzerViewModel>()
                //Log.d("ResourceCheck", "s_app_id: ${getString(R.string.prompt_user)}")
                // 初始化一下api单例
                SparkService.init(
                    appId = getString(R.string.s_app_id),
                    apiKey = getString(R.string.s_api_key),
                    apiSecret = getString(R.string.s_api_secret),
                    promptUser = getString(R.string.prompt_user),
                    context = this@MainActivity
                )
                // 初始化一下tts单例
                Tts.init(this@MainActivity)

                // 创建一个Launcher 来启动文件选择器
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument(),
                    onResult = { uri ->
                        if (uri != null) {
                            viewModel.updateVideoUri(uri)
                            viewModel.updateCurrentFrameNumber(0)
                            viewModel.updateAppStatus(AppStatus.ANALYZING)
                            // 协程分析视频
                            viewModel.analyzeVideo(context = this@MainActivity)
                        }
                    }
                )

                VideoAnalyzerApp(
                    analyzerViewModel = viewModel,
                    onImportVideoClick = {
                        launcher.launch(arrayOf("video/*"))
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart Called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        // 可能需要保留一些状态
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        // 释放语音引擎的资源
        Tts.ttsOnDestroy()
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }
}

