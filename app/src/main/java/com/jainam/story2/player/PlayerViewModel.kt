package com.jainam.story2.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.database.MyBook
import com.jainam.story2.utils.TTS
import com.jainam.story2.utils.Type
import kotlinx.coroutines.*
import com.jainam.story2.utils.Entities



class PlayerViewModel(
    application: Application,
   val lastPage: Int,
   val lastPosition: Int,
  val  uri: Uri,
  val  bookDatabaseDao: BookDatabaseDao,
   val language: String,
  val  bookLength: Int
) :
    AndroidViewModel(application) {




    val tts = TTS(application.applicationContext,lang = language,voiceName = bookDatabaseDao.getVoiceOfLang(language)?.voiceName)
    private var speakState = SpeakState.PAUSED

    private  val mBook: MyBook
        get() {
            return runBlocking {
                    val thumbnail = bookDatabaseDao.get(uriString = uri.toString())
                    val type:Type = Type.valueOf(thumbnail.type)

                    return@runBlocking  MyBook(uriAsString = uri.toString(),bookLength = bookLength,type = type,language = language)
              }
        }
    private val entities = Entities(mBook,application.applicationContext)
    val speed:MutableLiveData<Int> = MutableLiveData(100)
    val pitch:MutableLiveData<Int> = MutableLiveData(100)




    fun onSwipe(direction: OnSwipeListener.Direction) {
        when(direction){
            OnSwipeListener.Direction.LEFT->listenNextEntity(true)
            OnSwipeListener.Direction.Right->listenPreviousEntity()
            OnSwipeListener.Direction.UP->switchEntityForward()
            OnSwipeListener.Direction.DOWN->switchEntityBackward()
        }
    }

    private fun switchEntityBackward() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop(){}
            entities.switchEntityBackward()
            tts.speak(
                entities.currentEntity,
                {},
                {listenNextEntity(false)}
            )
        }
    }
    fun speak(){
        tts.speak(entities.currentEntity,{},{listenNextEntity(false)})
    }
    fun stopSpeaking(){
        tts.stop {  }
    }
    private fun switchEntityForward() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop(){}
            entities.switchEntityForward()
            tts.speak(
                entities.currentEntity,
                {},
               {listenNextEntity(false)}
            )
        }
    }

    fun onDoubleTap() {
        if (speakState === SpeakState.SPEAKING) {
            speakState = SpeakState.PAUSED
            tts.stop(){}
            return
        }else{
            speakState = SpeakState.SPEAKING
            tts.speak(
                entities.currentEntity,
                {},
                {listenNextEntity(false)}
            )
        }

    }

    private fun listenNextEntity(swiped: Boolean) {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop(){}
            if (!entities.isLastEntity) {
                entities.getNextEntity(swiped)
                tts.speak(
                    entities.currentEntity,
                    {},
                    {listenNextEntity(false)}
                )
            }
        }
    }

    private fun listenPreviousEntity() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop(){}
            entities.getPreviousEntity(true)
            tts.speak(
                entities.currentEntity,
                {},
                {listenNextEntity(false)}
            )
        }
    }










}
enum class SpeakState{
    SPEAKING,PAUSED
}