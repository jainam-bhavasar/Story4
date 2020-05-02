package com.jainam.story2.utils

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.collections.ArrayList

class TTS(
    private val context: Context
) {

    val tts = TextToSpeech(context, onInitListener(),"com.google.android.tts")
    val isSpeakingFinished = MutableLiveData(false)
    private var queuedText = " "
    private val tag = "TTS"
    private fun onInitListener(): TextToSpeech.OnInitListener {
        return TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialised.postValue(true)
                Log.d(tag, "onInitListener: $queuedText")
                speak(queuedText)
            }
        }
    }

    private var isTtsInitialised = MutableLiveData(false)


    fun speak(text:String){

        if (!isTtsInitialised.value!!){
            queuedText = text
            Log.d(tag, "speak: tts.speak() returned early ")
            return
        }



        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,params,"ID")

    }

    fun stop(){
        tts.stop()
    }






    //return all Voices
    fun getAllVoicesOfLang(lang :String):Map<String,ArrayList<Voice>>{
        runBlocking {
            while (tts.voices == null)delay(300)
            Log.d("tts","waiting")
        }

        val countryToItsVoicesMap = mutableMapOf<String,ArrayList<Voice>>()
        val voicesWithRequiredLang = tts.voices.filter { voice -> voice.locale.language == lang }
        for (voice in voicesWithRequiredLang){
            if (countryToItsVoicesMap.containsKey(voice.locale.country)){
                val newVoiceList = countryToItsVoicesMap[voice.locale.country]?.plus((voice))
                countryToItsVoicesMap[voice.locale.country] = newVoiceList as ArrayList<Voice>
            }else{
                countryToItsVoicesMap[voice.locale.country] = arrayListOf(voice)
            }
        }

        return countryToItsVoicesMap
    }





}