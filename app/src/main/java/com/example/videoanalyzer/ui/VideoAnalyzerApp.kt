package com.example.videoanalyzer.ui

import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.videoanalyzer.R
import com.example.videoanalyzer.enums.AppStatus
import com.example.videoanalyzer.models.HistoryList
import com.example.videoanalyzer.ui.viewModel.AnalyzerViewModel
import com.example.videoanalyzer.utils.Tts
import java.util.Date
import java.util.Locale

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

    // 带应用栏的基本布局
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
                            // 如果是从分析结果页面返回，则添加历史记录
                            if (analyzerUiState.appStatus == AppStatus.ANALYZED) {
                                // 以当前时间做名称
                                val sdf = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.getDefault())
                                analyzerViewModel.addHistory(
                                    history = HistoryList(
                                        uri = analyzerUiState.videoUri,
                                        name = sdf.format(Date()),
                                        textList = analyzerUiState.textList.toList()
                                    )
                                )
                            }
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
                        onImportVideoClick = onImportVideoClick,
                        changeFrameInterval = { frameInterval ->
                            analyzerViewModel.updateFrameInterval(frameInterval)
                        },
                        frameInterval = analyzerUiState.frameInterval,
                        tempHistoryList = analyzerUiState.tempHistoryList,
                        clickToShowHistoryList = { history ->
                            analyzerViewModel.updateAppStatus(AppStatus.ANALYZED)
                            analyzerViewModel.updateVideoUri(history.uri)
                            analyzerViewModel.updateAnalyzeResult(textList = history.textList)
                        }
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
                        textList = analyzerUiState.textList,
                        frameInterval = analyzerUiState.frameInterval
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
    onImportVideoClick: () -> Unit,
    changeFrameInterval: (Long) -> Unit,
    frameInterval: Long,
    tempHistoryList: List<HistoryList>,
    clickToShowHistoryList: (history: HistoryList) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.text_frameInterval_tip),
                textAlign = TextAlign.Left,
            )

            Spacer(modifier = Modifier.height(8.dp))

            SelectFrameIntervalButton(
                modifier = Modifier.fillMaxWidth(),
                frameInterval = frameInterval,
                changeFrameInterval = changeFrameInterval
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 提示该选项的后果
            TipText(
                frameInterval = frameInterval,
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 历史记录
            Text(
                text = stringResource(R.string.text_history),
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShowHistoryList(
                tempHistoryList = tempHistoryList,
                clickable = clickToShowHistoryList
            )

        }
    }
}

// 分析结果页面
@Composable
fun VideoAnalyzerResultScreen(
    textList: List<String>,
    frameInterval: Long,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 操作按钮
        item {
            ResultOperationButton(
                textList = textList,
                frameInterval = frameInterval
            )
        }
        // 结果项
        itemsIndexed(textList) { index, text ->
            val startSecond = index * frameInterval / 1000
            val endSecond = (index + 1) * frameInterval / 1000
            val wholeText = stringResource(id = R.string.result_item_format, startSecond, endSecond, text)
            ResultCard(
                text = wholeText,
                clickable = {
                    // 播放语音
                    Tts.speak(wholeText, wholeText)
                }
            )
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

// 结果项展示
@Composable
fun ResultCard(
    text: String,
    clickable: ()-> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{
                // 播放语音
                clickable()
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = stringResource(R.string.text_play),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = Int.MAX_VALUE // 允许显示多行文本
            )
        }
    }
}

// 选择帧间隔按钮
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectFrameIntervalButton(
    modifier: Modifier,
    frameInterval: Long,
    changeFrameInterval: (frameInterval:  Long) -> Unit
) {
    val options = listOf("5", "10", "30")

    SingleChoiceSegmentedButtonRow (
        modifier = modifier
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                onClick = { changeFrameInterval(label.toLong() * 1000L) },
                selected = label.toLong() * 1000L == frameInterval,
                label = { Text(
                    text = stringResource(R.string.text_frameInterval, label)
                ) }
            )
        }
    }
}

// 帧间隔的提示
@Composable
fun TipText(
    frameInterval: Long,
    textAlign: TextAlign
) {
    val text: String = when (frameInterval) {
        5000L -> stringResource(R.string.text_frameInterval_tip_low, frameInterval / 1000)
        10000L -> stringResource(R.string.text_frameInterval_tip_middle, frameInterval / 1000)
        30000L -> stringResource(R.string.text_frameInterval_tip_quick, frameInterval / 1000)
        else -> "没有选择帧间隔！"
    }
    Text(
        text = text,
        textAlign = textAlign,
        style = MaterialTheme.typography.titleSmall
    )
}

// 结果页面操作按钮
@Composable
fun ResultOperationButton(
    textList: List<String>,
    frameInterval: Long
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // 上下外边距
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val textPattern = stringResource(R.string.result_item_format)
            // 全部播放
            Button(
                onClick = {
                    textList.forEachIndexed { index, it ->
                        // 合成文本
                        val startSecond = index * frameInterval / 1000
                        val endSecond = (index + 1) * frameInterval / 1000
                        val text = String.format(textPattern, startSecond, endSecond, it)
                        Tts.speak(text, text)
                    }
                }
            ) {
                Text(stringResource(R.string.button_all_play))
            }

            Spacer(modifier = Modifier.width(16.dp)) // 按钮之间间隔

            // 取消播放
            Button(
                onClick = {
                    Tts.cancel()
                }
            ) {
                Text(stringResource(R.string.button_cancel_play))
            }
        }
    }
}

// TODO 历史记录展示
@Composable
fun ShowHistoryList(
    tempHistoryList: List<HistoryList>,
    clickable: (history: HistoryList) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tempHistoryList) { history ->
            historyCard(
                history = history,
                clickable = clickable
            )
        }
    }
}

@Composable
fun historyCard(
    history: HistoryList,
    clickable: (history: HistoryList) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{
                // 播放语音
                clickable(history)
            },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.settings_backup_restore),
                contentDescription = stringResource(R.string.text_history_item),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = history.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = Int.MAX_VALUE // 多行文本
            )
        }
    }
}

@Preview
@Composable
fun PreviewResultItem() {
    ResultCard("test...test...") {}
}
