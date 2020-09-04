package com.jainam.story2.player

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.database.MyBook
import com.jainam.story2.database.BookMetaData
import com.jainam.story2.home.FileDetail
import com.jainam.story2.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class PlayerViewModel(
    application: Application,
    val lastPage: Int,
    val lastPosition: Int,
    val uri: Uri,
    val bookDatabaseDao: BookDatabaseDao,
    val language: String,
    val bookLength: Int,
    val entity: Entity
) : AndroidViewModel(application) {


    val tts = TTS(
        application.applicationContext,
        lang = language,
        voiceName = bookDatabaseDao.getVoiceOfLang(language)?.voiceName
    )

    private val context: Context = application.applicationContext
    var speakState = SpeakState.PAUSED
    private val mBook: MyBook by lazy {
        val name: String? = FileDetail().getFileDetailFromUri(context, uri)!!.fileName
        val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))
        MyBook(
            uriAsString = uri.toString(),
            bookLength = bookLength,
            type = type,
            language = language
        )

    }
    private val entities: Entities by lazy { Entities(mBook, application.applicationContext,lastPage,entity) }
    val speed: MutableLiveData<Int> = MutableLiveData(100).apply {
        CoroutineScope(Dispatchers.Main).launch {
            this@apply.observeForever {
                tts.tts.setSpeechRate(it.toFloat() / 100)
                Log.d(TAG, "speed: $it")

            }
        }
    }
    val pitch: MutableLiveData<Int> = MutableLiveData(100).apply {
        CoroutineScope(Dispatchers.Main).launch {
            this@apply.observeForever {
                tts.tts.setPitch(this@apply.value!!.toFloat() / 100)
            }
        }
    }


    fun onSwipe(direction: OnSwipeListener.Direction) {
        when (direction) {
            OnSwipeListener.Direction.LEFT -> listenNextEntity(true)
            OnSwipeListener.Direction.Right -> listenPreviousEntity()
            OnSwipeListener.Direction.UP -> switchEntityForward()
            OnSwipeListener.Direction.DOWN -> switchEntityBackward()
        }
    }

    fun onDoubleTap() {
        if (speakState === SpeakState.SPEAKING) {
            speakState = SpeakState.PAUSED
            tts.stop {}
            return
        } else {
            speakState = SpeakState.SPEAKING
            tts.speak(
                entities.currentEntity,
                {},
                { listenNextEntity(false) }
            )
        }

    }

    fun speak() {
        tts.speak(entities.currentEntity, {}, { listenNextEntity(false) })
    }

    fun stopSpeaking() {
        tts.stop { }
    }


    private fun switchEntityForward() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop {}
            entities.switchEntityForward()
            tts.speak("${entities.mCurrentEntityName} mode", {},
                {
                    tts.speak(
                        entities.currentEntity,
                        {},
                        { listenNextEntity(false) }
                    )
                })

        }
    }

    private fun switchEntityBackward() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop {}
            entities.switchEntityBackward()

            tts.speak("${entities.mCurrentEntityName} mode", {},
                {
                    tts.speak(
                        entities.currentEntity,
                        {},
                        { listenNextEntity(false) }
                    )
                })
        }
    }

    private fun listenNextEntity(swiped: Boolean) {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop {}
            if (!entities.isLastEntity) {
                entities.getNextEntity(swiped)
                tts.speak(
                    entities.currentEntity,
                    {},
                    { listenNextEntity(false) }
                )
            }
        }
    }

    private fun listenPreviousEntity() {
        if (speakState === SpeakState.SPEAKING) {
            tts.stop {}
            entities.getPreviousEntity()
            tts.speak(
                entities.currentEntity,
                {},
                { listenNextEntity(false) }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSpeaking()
        CoroutineScope(Dispatchers.IO).launch {
            bookDatabaseDao.updateMyVoice(MyVoice(language,tts.tts.voice.locale.country,tts.tts.voice.name))

            val bookMetaData:BookMetaData = bookDatabaseDao.get(uriString = uri.toString() )
            bookMetaData.lastPage = entities.pageNum
            bookMetaData.lastPosition = 0
            bookMetaData.entity = entities.mCurrentEntityName.toString()
            bookDatabaseDao.update(bookMetaData)
        }
    }

}

enum class SpeakState {
    SPEAKING, PAUSED
}