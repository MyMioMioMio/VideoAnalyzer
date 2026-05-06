# 适用于盲人的视频分析器

## 一、基本功能
- 视频分割提取关键帧
- 用文字对关键帧进行整体描述（讯飞星火图像理解模型）
- 用语音播放文字描述
- 可选择帧间隔（5秒 / 10秒 / 30秒）
- 分析历史记录

## 二、实现思路
1. 视频分割提取关键帧：使用 Android 的 MediaMetadataRetriever 对视频按帧间隔提取关键帧。
2. 图片文字描述：将关键帧发送至讯飞星火图像理解模型获取文本结果。
3. 语音播放：使用 Android 内置的 TextToSpeech API 播放文本结果。
4. 历史记录：分析完成后自动保存记录，支持点击回看。

## 三、项目结构

```
VideoAnalyzer/
├── app/
│   ├── libs/
│   │   └── SparkChain.aar              # 讯飞星火 SDK
│   └── src/main/java/com/example/videoanalyzer/
│       ├── MainActivity.kt            # 入口 Activity
│       ├── enums/
│       │   └── AppStatus.kt           # 应用状态枚举
│       ├── models/
│       │   └── HistoryList.kt         # 历史记录数据类
│       ├── service/
│       │   └── SparkService.kt        # 星火 API 封装
│       ├── ui/
│       │   ├── VideoAnalyzerApp.kt    # Compose 界面组件
│       │   ├── state/
│       │   │   └── AnalyzerUiState.kt # UI 状态数据类
│       │   ├── theme/                 # Material3 主题
│       │   └── viewModel/
│       │       └── AnalyzerViewModel.kt # 核心业务逻辑
│       └── utils/
│           └── Tts.kt                # TextToSpeech 封装
├── build.gradle.kts
└── settings.gradle.kts
```

## 四、技术栈
- **语言**: Kotlin
- **UI**: Jetpack Compose + Material3
- **架构**: MVVM (ViewModel + StateFlow)
- **AI**: 讯飞星火 SparkChain SDK
- **语音**: Android TextToSpeech
- **最低 SDK**: 28 (Android 9)

## 五、部署前提
1. 注册讯飞星火开放平台账号，获取 `app_id`、`api_key`、`api_secret`。
2. 在 `app/src/main/res/values/` 下创建 `config.xml`，填入凭据。
3. 确保 `app/libs/SparkChain.aar` 存在（星火 SDK 本地依赖）。

### config.xml 示例

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="prompt_system">你现在是一个画面描述助手</string>
    <string name="prompt_user">请用中文简要描述这张图的画面，如果没有内容请回复'无图像'，50字以内</string>
    <!-- 星火大模型的配置信息 -->
    <string name="s_app_id">你的app_id</string>
    <string name="s_api_key">你的api_key</string>
    <string name="s_api_secret">你的api_secret</string>
</resources>
```
