package com.jainam.story2.player.voice

import android.speech.tts.Voice
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.utils.MyVoice
import com.jainam.story2.utils.TTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception


class VoiceViewModel(var voice:Voice, tts: TTS,val bookDatabaseDao: BookDatabaseDao) :ViewModel() {

    val country = MutableLiveData(voice.locale.country)
    private val language: String = voice.locale.language
    val voicesOfLang = MutableLiveData(tts.getAllVoicesOfLang(language))

    val voicesOfLangAndCountry = MediatorLiveData<List<Voice>>().also { liveData->
        liveData.addSource(country){
            liveData.postValue(voicesOfLang.value!![it])
        }
        liveData.addSource(voicesOfLang){
            liveData.postValue(it[country.value!!])
        }
    }

    fun isOfflineVoiceNotInstalled(voice: Voice):Boolean{
       return voice.features.contains("notInstalled")
    }

    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch {
            val myVoice:MyVoice? = bookDatabaseDao.getVoiceOfLang(language)

            if (myVoice == null) {
                bookDatabaseDao.insertMyVoice(MyVoice(language = language,countryCode = country.value!!,voiceName = voice.name))
            }else{
                bookDatabaseDao.updateMyVoice(myVoice)
            }

        }


    }



}
class VoiceViewModelFactory(private val voice:Voice, private val tts: TTS, private val bookDatabaseDao: BookDatabaseDao):
    ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VoiceViewModel::class.java)){
            return VoiceViewModel(voice = voice,tts = tts,bookDatabaseDao = bookDatabaseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

