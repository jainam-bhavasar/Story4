package com.jainam.story2.services

import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.jainam.story2.player.PlayerViewModel
import com.jainam.story2.utils.GetText
import com.jainam.story2.utils.NavigateBy
import com.jainam.story2.utils.TTS
import kotlinx.coroutines.*
import java.util.*
import kotlin.math.log

class LocalService : LifecycleService() {
    val tag = "LocalService"
    // Binder given to clients
    private val binder = LocalBinder()

    // Random number generator
    private val mGenerator = Random()

     lateinit var tts :TTS
    lateinit var viewModel: MutableLiveData<PlayerViewModel>


    //current position of text card
     val currentTextPosition = MutableLiveData<Int>(0)

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): LocalService = this@LocalService
    }


    //uri variable to be initialised
    private lateinit var uri:Uri

    //getTextVariable
    private lateinit var getText: GetText

    //max book length variable
    val bookLength = MutableLiveData<Int>(0)

    //current page number
    val currentPageNumber = MutableLiveData<Int>(1)

    // current page's text
    val currentPageTextList = MediatorLiveData<List<String>>()

    //current page Text sizes List
    val currentPageTextSizes = MediatorLiveData<List<Int>>()

    val language = MutableLiveData<String>("en")
    override fun onCreate() {
        super.onCreate()

    }

    val speechRate=MutableLiveData(1.toFloat())



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val extras = intent!!.extras
        Log.d(tag, "onStartCommand: $extras")



        //initiating text to speech
        tts =  TTS(applicationContext)

        // getting the uri
        uri  = Uri.parse(extras!!["UriAsString"].toString())

        //in the background thread getting the viewmodel and inititialising other properties
        CoroutineScope(Dispatchers.Main).launch {
            viewModel = withContext(Dispatchers.Default){MutableLiveData(PlayerViewModel(applicationContext,uri))}

            //observe the viewmodel and change properties according to it
            viewModel.observe(this@LocalService, Observer {
                bookLength.postValue(it.bookLength)

                //setting the language
                if (it.book2.language == "und"){
                    Toast.makeText(applicationContext,"Sorry, cannot detect Language " ,Toast.LENGTH_SHORT).show()
                }else{
                    val ourLocale = Locale(it.book2.language)
                    Log.d(tag, "onStartCommand: ourLocale -> $ourLocale")
                    Log.d(tag, "onStartCommand: tts languages :- ${tts.tts.availableLanguages}")
                    Log.d("is", "onStartCommand: is lang available ${tts.tts.isLanguageAvailable(ourLocale)}")
                    if (tts.tts.isLanguageAvailable(ourLocale) == TextToSpeech.LANG_AVAILABLE){
                        tts.tts.language = ourLocale
                        Log.d(tag, "onStartCommand: ${tts.tts.language}")
                        Log.d(tag, "onStartCommand: ${tts.tts.voice}")
                    }else{
                        Toast.makeText(applicationContext,"Sorry, Google text to speech doesn't have this language " ,Toast.LENGTH_SHORT).show()
                    }
                }

            })


            currentPageTextList.addSource(currentPageNumber){
                currentPageTextList.postValue(viewModel.value!!.getCurrentPage(it).splitPageTextByNavigationType(NavigateBy.SENTENCE))
            }
            currentPageTextSizes.addSource(currentPageTextList){
                val newList  = Array<Int>(it.size) { i -> it[i].length}.toList()
                Log.d(tag, "onStartCommand: new list $newList")
                currentPageTextSizes.postValue(newList)
            }



          //  /observing current position of text and speaking what is currently visible


        }


        return super.onStartCommand(intent, flags, startId)
    }



    fun speak(positionOfTextInPage:Int){

        stopTTS()
        Log.d(tag, "speak: current pagetextlist value is${currentPageTextList.value}")
        Log.d(tag, "speak: current position value is${positionOfTextInPage}")


         tts.speak(currentPageTextList.value!![positionOfTextInPage])

        runBlocking { delay(100) }
        tts.tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                Log.d("tts","speaking done $positionOfTextInPage")
                if (currentPageNumber.value!! < bookLength.value!!){
                    if (positionOfTextInPage < currentPageTextList.value?.size!!-1) {
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                currentTextPosition.value = currentTextPosition.value?.plus(1)
                            }
                            speak(currentTextPosition.value!!)
                        }
                    }else{
                        runBlocking {
                            withContext(Dispatchers.Main) {
                                currentPageNumber.value = currentPageNumber.value?.plus(1)
                                currentTextPosition.postValue(0)
                            }
                            speak(currentTextPosition.value!!)
                        }
                    }

                }





            }

            override fun onError(utteranceId: String?) {
                Log.d("tts","speaking error")

            }

            override fun onStart(utteranceId: String?) {
                Log.d("tts","speaking started")

            }

        })
    }


    fun positionAfterForward10Seconds(list: List<Int>,position:Int):Int{
        var resultPosition = position
        var textLength = list[resultPosition]
        while ((textLength < 200) and (resultPosition < (list.size - 1))){
            resultPosition+=1
            textLength += list[resultPosition]
        }
       return if (resultPosition == (list.size - 1)) -1 else resultPosition

    }

    fun positionAfterBackward10Seconds(list: List<Int>,position:Int):Int{
        var resultPosition = position
        var textLength = list[resultPosition]
        while ((textLength < 200) and (resultPosition > 0)){
            resultPosition-=1
            textLength += list[resultPosition]
        }
        return if (resultPosition == 0) -1 else resultPosition

    }



    //function to stop text to speech
    fun stopTTS(){
        tts.stop()
    }




    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy: destroed")
        stopTTS()
        tts.tts.shutdown()
        this.stopSelf()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(tag, "onTaskRemoved: ")
        tts.stop()
        stopTTS()
        stopService(rootIntent)
        this.stopSelf()
    }
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        //tts.stop()
        return super.onUnbind(intent)

    }

    private fun String.splitPageTextByNavigationType(navigateBy: NavigateBy):List<String>{
        return  when(navigateBy){
            NavigateBy.SENTENCE -> replace("\n".toRegex()," ").split(".")
            NavigateBy.PARAGRAPH -> split(".\n")
            NavigateBy.LINE -> split("\n")
            NavigateBy.PAGE ->  listOf(this)
        }
    }

}

