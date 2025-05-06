package com.example.videoanalyzer.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.videoanalyzer.R
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.ui.viewModel.AnalyzerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoAnalyzerApp(
    analyzerViewModel: AnalyzerViewModel,
    onImportVideoClick: () -> Unit
) {
    // 追踪状态
    val analyzerUiState by analyzerViewModel.uiState.collectAsState()
    // 滚动行为
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection), // 设置滚动保持
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer, // 设置顶部颜色
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer // 设置标题颜色
                ),
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (analyzerUiState.appStatus == AppStatus.WAIT_FOR_IMPORT_VIDEO) {
                        // 待导入视频时，显示菜单按钮
                        IconButton(onClick = {
                            // TODO 点击 Menu 图标的逻辑
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = stringResource(R.string.top_menu)
                            )
                        }
                    } else if (analyzerUiState.appStatus != AppStatus.ANALYZING) {
                        // 分析视频时，显示返回按钮
                        IconButton(onClick = {
                            // 点击返回按钮的逻辑
                            // TODO 需要清楚上次的结果等等
                            // 设置状态为 WAIT_FOR_IMPORT_VIDEO
                            analyzerViewModel.updateAppStatus(AppStatus.WAIT_FOR_IMPORT_VIDEO)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // 右侧操作按钮的逻辑
                    }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = stringResource(R.string.more_selection)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = { contentPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
            ) {
                if (analyzerUiState.appStatus == AppStatus.WAIT_FOR_IMPORT_VIDEO) {
                    // 带导入视频，显示首页
                    VideoAnalyzerMainScreen(
                        contentPadding = contentPadding,
                        videoUri = analyzerUiState.videoUri,
                        onImportVideoClick = onImportVideoClick
                    )
                } else if (analyzerUiState.appStatus == AppStatus.ANALYZING) {
                    // 分析视频中，显示圆形进度条，和提示字幕
                    VideoAnalyzerProgress(
                        contentPadding = contentPadding,
                        currentFrameNumber = analyzerUiState.currentFrameNumber,
                        frameTotal = analyzerUiState.frameTotal
                    )

                } else if (analyzerUiState.appStatus == AppStatus.ANALYZED) {
                    //  分析视频完成，显示分析结果
                    VideoAnalyzerResultScreen(
                        contentPadding = contentPadding,
                        videoUri = analyzerUiState.videoUri,
                        textList = analyzerUiState.textList
                    )
                }

            }
        }
    )
}

// 主页面
@Composable
fun VideoAnalyzerMainScreen(
    contentPadding: PaddingValues,
    videoUri: Uri,
    onImportVideoClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // 顶部按钮, 圆形按钮
        Button(
            onClick = onImportVideoClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.import_video),
                style = MaterialTheme.typography.titleLarge // 设置文字样式
            )
        }

        // 其他内容
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "其他内容区域")
        }
    }
}

// 分析结果页面
@Composable
fun VideoAnalyzerResultScreen(
    contentPadding: PaddingValues,
    videoUri: Uri,
    textList: List<String>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            Text(
                text = "视频Uri：${videoUri.lastPathSegment}",
                modifier = Modifier.padding(16.dp)
            )
        }
        items(textList) { text ->
            Text(text = text, modifier = Modifier.padding(16.dp))
        }
    }
}

// 分析视频中加载页面
@Composable
fun VideoAnalyzerProgress(
    contentPadding: PaddingValues,
    currentFrameNumber: Int,
    frameTotal: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val percent = currentFrameNumber.toFloat() / frameTotal

        CircularProgressIndicator(
            progress = { percent },
            modifier = Modifier.width(100.dp)
        )

        // 间距
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = stringResource(R.string.loading_text, "${(percent * 100).toInt()}%"),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = "需要分析的总帧数${ frameTotal }"
        )
        // TODO 执行语音提示
    }
}
