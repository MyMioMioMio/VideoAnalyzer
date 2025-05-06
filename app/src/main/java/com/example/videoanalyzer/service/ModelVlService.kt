package com.example.videoanalyzer.service
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost
import kotlin.io.encoding.Base64
import kotlin.time.Duration.Companion.seconds

object ModelVlService {
    private lateinit var openAI: OpenAI
    private lateinit var promptSystem: String
    private lateinit var promptUser: String
    private lateinit var modelId: String

    // 初始化单例对象中的属性
    fun init(
        apiKey: String,
        apiUrl: String,
        promptSystem: String,
        promptUser: String,
        modelId: String
    ) {
        openAI = OpenAI(
            token = apiKey,
            host = OpenAIHost(baseUrl = "$apiUrl/v1"),
            timeout = Timeout(socket = 60.seconds)
        )
        this.promptSystem = promptSystem
        this.promptUser = promptUser
        this.modelId = modelId
    }

    suspend fun chatWithApi(base64: String) : String {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(modelId),
            messages = listOf(
                ChatMessage(
                    role = ChatRole.System,
                    content = promptSystem
                ),
                ChatMessage(
                    role = ChatRole.User,
                    content = """
                    [
                        {
                            "type": "image_url",
                            "image_url": {"url": "data:image/jpeg;base64,${base64}"}
                        },
                        {
                            "type": "text",
                            "text": "$promptUser"
                        }
                    ]
                    """.trimIndent()
                )
            )
        )
        val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
        return completion.choices.first().message.content.toString()
    }

}
