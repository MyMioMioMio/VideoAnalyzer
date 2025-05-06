package com.example.videoanalyzer.service

import android.content.Context
import android.util.Log
import com.iflytek.sparkchain.core.LLMConfig
import com.iflytek.sparkchain.core.LLMFactory
import com.iflytek.sparkchain.core.SparkChain
import com.iflytek.sparkchain.core.SparkChainConfig


object SparkService {
    private lateinit var config: SparkChainConfig
    //private lateinit var image_llm: LLM
    private lateinit var promptUser: String

    fun init(
        appId: String,
        apiKey: String,
        apiSecret: String,
        promptUser: String,
        context: Context
    ) {
        this.promptUser = promptUser
        // sdk初始化
        config = SparkChainConfig.builder()
            .appID(appId)
            .apiKey(apiKey)
            .apiSecret(apiSecret)
        val ret = SparkChain.getInst().init(context, config)
        Log.d("SparkService", "init ret: $ret")
    }


    // 运行llm
    suspend fun runLLm(byteArray: ByteArray): String {
        // llm初始化
        val image_llm = LLMFactory.imageUnderstanding(LLMConfig.builder())
        val output = image_llm.run(promptUser, byteArray)
        Log.d("SparkService", "runLLm output: ${output.errCode}, ${output.errMsg}")
        if (output.errCode != 0) {
            return output.errMsg
        }
        return image_llm.run(promptUser, byteArray).content
    }

}
