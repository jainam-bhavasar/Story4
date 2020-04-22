package com.jainam.story2.player.voiceSeclection

import android.app.Application
import android.speech.tts.Voice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.utils.TTS

class VoiceSelectionMenuViewModel( application: Application) : AndroidViewModel(application) {
    private val tts = TTS(application.applicationContext)

    fun getVoices():Map<String,ArrayList<Voice>>{
        return tts.getAllVoicesOfLang("en")
    }

    val allVoices = tts.allVoicesOfLang



}
