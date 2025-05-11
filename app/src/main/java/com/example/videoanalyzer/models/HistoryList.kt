package com.example.videoanalyzer.models

import android.net.Uri

// 封装的历史数据类
data class HistoryList(
    var uri: Uri,
    var name: String,
    var textList: List<String>
)
