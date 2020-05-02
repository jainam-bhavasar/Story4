package com.jainam.story2.player.voiceSeclection

import android.speech.tts.Voice
import androidx.lifecycle.*
import com.jainam.story2.utils.TTS
import java.lang.IllegalArgumentException

class VoiceViewModel(private val tts:TTS, private val language:String) :ViewModel() {

     private fun getVoicesOfLang(lang: String):Map<String,ArrayList<Voice>> {
        return tts.getAllVoicesOfLang(lang)
    }

    val country = MutableLiveData<String>()
    val gender = MutableLiveData<Gender>()
    val voicesOfLang = MutableLiveData(tts.getAllVoicesOfLang(language))

    val voicesOfLangAndCountry = MediatorLiveData<List<Voice>>().also { livedata->
        livedata.addSource(country){
            livedata.postValue(voicesOfLang.value!![it])
        }
        livedata.addSource(voicesOfLang){
            livedata.postValue(it[country.value!!])
        }
    }


    //get all the voices of a country and language ->
     fun voicesOf(country:String, lang: String,gender: Gender):List<Voice>{
        val allCountrySpecificVoices =  getVoicesOfLang(lang)[country]
        return if (allCountrySpecificVoices != null){
            if (gender==Gender.MALE){
                allCountrySpecificVoices.filter { it.name.contains("#male")  }
            }else{
                allCountrySpecificVoices.filter { !it.name.contains("#male")  }
            }
        }else{
            emptyList()
        }

    }


}
class VoiceViewModelFactory( private val tts: TTS,private val language: String):
    ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VoiceViewModel::class.java)){
            return VoiceViewModel(tts,language) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
enum class Gender{
    MALE,FEMALE
}