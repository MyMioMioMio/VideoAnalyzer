package com.example.videoanalyzer.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale


object Tts {
    private lateinit var tts: TextToSpeech

    fun init(
        context: Context
    ) {
        this.tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.CHINESE)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("Tts", "Language not supported")
                }
            }
        }
    }

    fun speak(text: String, utteranceId: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
    }
}
