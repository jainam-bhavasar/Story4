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

    private fun onInitListener(): TextToSpeech.OnInitListener {
        return TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialised.postValue(true)
                speak(queuedText)
              //  allVoicesOfLang.postValue(getAllVoicesOfLang("en"))
            }
        }
    }

    private var isTtsInitialised = MutableLiveData(false)
    private var queuedText = ""


    fun speak(text:String){

        if (!isTtsInitialised.value!!){
            queuedText = text
            return
        }

        tts.setOnUtteranceProgressListener(MyUtteranceProgressListener)
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH,params,"ID")

    }


    //utterance progress listener
    object MyUtteranceProgressListener : UtteranceProgressListener() {
        override fun onDone(utteranceId: String?) {
                Log.d("tts","sythesization done")

        }

        override fun onError(utteranceId: String?) {
                Log.d("tts","sythesization error")

        }

        override fun onStart(utteranceId: String?) {
                Log.d("tts","sythesization started")

        }

    }

    val allVoicesOfLang = MutableLiveData<Map<String,ArrayList<Voice>>>()


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
        Log.d("voices",countryToItsVoicesMap.toString())
        return countryToItsVoicesMap
    }





}