package com.jainam.story2.player

import android.app.Application
import android.content.Context
import android.net.Uri
import android.speech.tts.UtteranceProgressListener
import android.view.accessibility.AccessibilityManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.database.Book1
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.home.FileDetail
import com.jainam.story2.utils.GetText
import com.jainam.story2.utils.TTS
import com.jainam.story2.utils.Type
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class PlayerViewModel(application: Application,
                      var lastPage :Int,
                      var lastPosition:Int,
                      val uri: Uri,
                      val bookDatabaseDao: BookDatabaseDao,
                      val language:String, val bookLength:Int = 1) :AndroidViewModel(application){
    private val TAG: String = "PVM2"
    private val tag = "pvm2"
    private val context = application.applicationContext
    //If database query fails then initialise a book with the same uri and use this
    private val book2: Book1 by lazy {
        val name = FileDetail().getFileDetailFromUri(context, uri)!!.fileName
        val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))

        val totalPages = bookLength

        val language  = language
        Book1(
            uriAsString = uri.toString(),
            bookName = name,
            type = type,
            bookLength = totalPages,
            language = language
        )
    }

    private var book1: Book1? = null
    //initiating read pdf class if type is pdf

    private val getText: GetText = GetText(book1?.type?:book2.type, context.contentResolver.openInputStream(uri)!!)

    //current page number
    val currentPageNum =  MutableLiveData(lastPage)


    //live data of current position
    val currentPosition  = MutableLiveData(lastPosition)

    //current page text list
    val currentPageTextList = MediatorLiveData<List<String>>().apply {

        addSource(currentPageNum){num ->
            num?.let {  this.postValue(getPageList(num))}
        }
        CoroutineScope(Dispatchers.Main).launch {
            observeForever {  }
        }
    }

    //initialising tts
    val tts = TTS(application.applicationContext,book2.language,bookDatabaseDao.getVoiceOfLang(book2.language)?.voiceName)
    //TTS speak -state live data'
    val speakState =  MutableLiveData(SpeakState.PAUSED)
    //stopped by user
    val speedConstantArray : ArrayList<Float> = ArrayList()

    var  startTime :Long = 0L
    private  var accessibilityManager: AccessibilityManager =      context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager


    //tts speaking
    fun speak(startCharacterPosition: Int = 0){
        tts.speak(currentPageTextList.value!![currentPosition.value!!].substring(startCharacterPosition))
        tts.tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {


                if (currentPosition.value!! < currentPageTextList.value!!.size - 1) {

                    currentPosition.postValue(currentPosition.value!! + 1)
                }else{
                    if (currentPageNum.value!! < bookLength){
                        currentPageTextList.postValue(getPageList(currentPageNum.value!! +1))
                        currentPageNum.postValue(currentPageNum.value!! +1)
                        currentPosition.postValue(0)
                    }

                }


            }
            override fun onStop(utteranceId: String?, interrupted: Boolean) {
                super.onStop(utteranceId, interrupted)

                if (!stoppedByUser){
                    val differenceInMilliSeconds = (System.nanoTime()-startTime)/1000000
                    val positionCalculatedByTime = (differenceInMilliSeconds/63.0).toInt()
                    val currentSentenceLength = currentPageTextList.value!![currentPosition.value!!].length
                    val substringStartPosition= if( positionCalculatedByTime< (currentSentenceLength-1))positionCalculatedByTime else currentSentenceLength-1
                    if (speakState.value!! != SpeakState.PAUSED){
                        speak(substringStartPosition)
                    }
                }else{
                    if (accessibilityManager.isEnabled and accessibilityManager.isTouchExplorationEnabled){
                        stoppedByUser = false
                    }

                }

            }

            override fun onError(utteranceId: String?) {
                tts.stop()
                speakState.value = SpeakState.PAUSED
            }

            override fun onStart(utteranceId: String?) {
                startTime = System.nanoTime()

            }

        })
    }
    //stopped by user variable
    private var stoppedByUser:Boolean = false

    // stop speaking
    fun stopSpeaking() {
        stoppedByUser = true
        tts.stop()
    }
    val speed  = MutableLiveData(100).apply {
        CoroutineScope(Dispatchers.Main).launch {
            this@apply.observeForever {
                tts.tts.setSpeechRate(this@apply.value!!.toFloat()/100)
            }
        }
    }
    val pitch  = MutableLiveData(100).apply {
        CoroutineScope(Dispatchers.Main).launch {
            this@apply.observeForever {
                tts.tts.setPitch(this@apply.value!!.toFloat()/100)
            }
        }
    }


    //function to increase the current page number till the end comes
    fun increaseCurrentPageNumTillEnd() {
        if (currentPageNum.value!! < book2.bookLength ) {
            currentPageNum.value = currentPageNum.value!!.plus(1)
            currentPosition.postValue(0)
        }

    }

    //function to increase the current page number till the start comes
    fun decreasePageNum() {
        if (currentPageNum.value!! > 1) {
            currentPageNum.value = currentPageNum.value!!.minus(1)
            currentPosition.postValue(0)
        }
    }



    //10 seconds forward method
    fun forward10Seconds( position: Int){
        var resultPosition = position
        var textSize = 0
        var pageTextList = currentPageTextList.value!!
        while (textSize<200){
            textSize += pageTextList[resultPosition].length
            if (resultPosition < (pageTextList.size -1)){
                resultPosition += 1
            }else{
                increaseCurrentPageNumTillEnd()
                pageTextList = getPageList(currentPageNum.value!!)
                currentPageTextList.postValue(pageTextList)
                currentPosition.postValue(0)
                resultPosition = 0
            }
        }
        currentPosition.postValue(resultPosition)

    }


    //10 seconds backward position
    fun backward10Seconds( position: Int){
        var resultPosition = position
        var textSize = 0
        var pageTextList = currentPageTextList.value!!
        while (textSize<200){
            textSize += pageTextList[resultPosition].length
            if (resultPosition > 0){
                resultPosition -= 1
            }else{
                decreasePageNum()
                pageTextList = getPageList(currentPageNum.value!!)
                currentPageTextList.postValue(pageTextList)
                resultPosition = getPageList(currentPageNum.value!!).size - 1
                currentPosition.postValue(resultPosition)
            }
        }

        currentPosition.postValue(resultPosition)
    }


    private val languageFullStopCharacter:Char =
        when(book2.language){
            "en" -> '.'
            "hi" -> '।'   //hindi
            "bn" -> '।'   //bengali
            "ur" ->'¯'   //urdu
            else -> '.'
        }


    //getting the page list
    fun getPageList(pageNum: Int):List<String>{
        return getText.getTextAtPage(pageNum).replace('\n',' ').split(languageFullStopCharacter).filter { it != "" }
    }


    override fun onCleared() {
        super.onCleared()
        CoroutineScope(Dispatchers.IO).launch {
            val thumbnail = bookDatabaseDao.get(uriString = uri.toString() )


            lastPage = this@PlayerViewModel.currentPageNum.value!!
            lastPosition = this@PlayerViewModel.currentPosition.value!!
            thumbnail.lastPage = lastPage
            thumbnail.lastPosition = lastPosition
            bookDatabaseDao.update(thumbnail)
        }

    }


}

enum class SpeakState{
    SPEAKING,PAUSED
}