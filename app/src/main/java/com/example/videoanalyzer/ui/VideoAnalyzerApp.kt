package com.example.videoanalyzer.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.ui.viewModel.AnalyzerViewModel
import java.util.Collections.list

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
                        text = "视频分析器",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (analyzerUiState.appStatus == AppStatus.WAIT_FOR_IMPORT_VIDEO) {
                        // 待导入视频时，显示菜单按钮
                        IconButton(onClick = {
                            // 点击 Menu 图标的逻辑
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "菜单"
                            )
                        }
                    } else if (analyzerUiState.appStatus == AppStatus.ANALYZING) {
                        // 分析视频时，显示返回按钮
                        IconButton(onClick = {
                            // 点击返回按钮的逻辑
                            // 设置状态为 WAIT_FOR_IMPORT_VIDEO
                            analyzerViewModel.updateAppStatus(AppStatus.WAIT_FOR_IMPORT_VIDEO)
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
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
                            contentDescription = "更多选项"
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
                if (analyzerUiState.appStatus == AppStatus.WAIT_FOR_IMPORT_VIDEO)
                {
                    // 带导入视频，显示首页
                    VideoAnalyzerMainScreen(
                        contentPadding = contentPadding,
                        videoUri = analyzerUiState.videoUri,
                        onImportVideoClick = onImportVideoClick
                    )
                } else if (analyzerUiState.appStatus == AppStatus.ANALYZING) {
                    //  分析视频，显示分析结果
                    VideoAnalyzerResultScreen(
                        contentPadding = contentPadding,
                        videoUri = analyzerUiState.videoUri,
                        textList = List<String>(50) { "test" }
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
                .fillMaxWidth() // 填充整行
                .aspectRatio(1f) // 设置宽高比为 1:1（正方形）
                .padding(16.dp), // 添加内边距
        ) {
            Text(
                text = "导入视频",
                style = MaterialTheme.typography.titleLarge // 设置文字样式
            )
        }

        // 其他内容（如果需要）
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
            Text(text = "视频Uri：${videoUri.lastPathSegment}", modifier = Modifier.padding(16.dp))
        }
        items(textList) { text ->
            Text(text = text, modifier = Modifier.padding(16.dp))
        }
    }
}
