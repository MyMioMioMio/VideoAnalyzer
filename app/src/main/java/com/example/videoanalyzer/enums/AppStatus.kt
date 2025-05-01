package com.example.videoanalyzer.enums

// 表示 App 的状态
enum class AppStatus {
    // 等待导入视频
    WAIT_FOR_IMPORT_VIDEO,
    // 正在分析视频
    ANALYZING,
    // 分析完成
    ANALYZED,
    // 出现错误
    ERROR
}
