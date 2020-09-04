package com.jainam.story2.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import nl.siegmann.epublib.epub.NCXDocument
import java.util.*
import kotlin.jvm.internal.Intrinsics


class TTS(
    context: Context,
    val lang: String,
    private val voiceName: String?
) {

    val tts = TextToSpeech(context, onInitListener())
    private var queuedText = " "
    private val tag = "TTS"
    private fun onInitListener(): TextToSpeech.OnInitListener {
        return TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsInitialised.postValue(true)
                Log.d(tag, "onInitListener: $queuedText")
                speak(queuedText,{},{})

                //setting th language to our required language it its available
                if (tts.isLanguageAvailable(Locale(lang)) >= 0){
                    tts.language = Locale(lang)

                  // setting the voice with the name that we saved in database if that voice exists
                 tts.getVoiceWithName(voiceName)?.let {
                     tts.voice = it
                 }

                }

                //logging out the language of tts
            }
        }
    }

    private var isTtsInitialised = MutableLiveData(false)


    fun speak(text: String?, onStartCallBack: ()->Unit, onDoneCallback: ()->Unit) {

        val value = isTtsInitialised.value
        if (!value!!) {
            queuedText = text!!
            Log.d(tag, "speak: tts.speak() returned early ")
            return
        }
        val params = Bundle()
        params.putString("utteranceId", "")
        tts.speak(text, 0, params, "ID")
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                onStartCallBack()
            }

            override fun onDone(utteranceId: String?) {
                onDoneCallback()
            }

            override fun onError(utteranceId: String?) {
                Log.d(TAG, "onError: text to speech error ")
            }
        })
    }

    fun stop(onDoneCallback: Function0<Unit>) {
        Intrinsics.checkNotNullParameter(onDoneCallback, "onDoneCallback")
        tts.stop()
        onDoneCallback.invoke()
    }






    //return all Voices
    fun getAllVoicesOfLang(lang: String):Map<String, ArrayList<Voice>>{
        runBlocking {
            while (tts.voices == null)delay(300)
            Log.d("tts", "waiting")
        }

        val countryToItsVoicesMap = mutableMapOf<String, ArrayList<Voice>>()
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

    private fun TextToSpeech.getVoiceWithName(name: String?):Voice?{
        if (name != null){
            for (voice in this.voices){
                if (voice.name == name) return voice
            }
            return null
        }else return null

    }





}