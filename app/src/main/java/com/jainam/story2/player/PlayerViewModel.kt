package com.jainam.story2.player

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.database.Book1
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.database.Pages
import com.jainam.story2.home.FileDetail
import com.jainam.story2.utils.*
import kotlinx.coroutines.*
import java.io.File
import java.io.IOException
import java.util.*


class PlayerViewModel(application: Application, val context: Context, private val uri: Uri, private var databaseDao: BookDatabaseDao) : AndroidViewModel(application),Book1Interface,GetLang {



    //defining scopes
    private var viewModelJob = Job()
    private val workScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)



    //If database query fails then initialise a book with the same uri and use this
    private val book2: Book1 by lazy {
        val name = FileDetail().getFileDetailFromUri(context, uri)!!.fileName
        val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))
        val getText1 = GetText(type, context.contentResolver.openInputStream(uri)!!)
        val totalPages = getText1.getTotalPages()
        val language  = getText1.getLanguage()
        val pages = Array(totalPages){" "}
        val isPageAvailableArray = Array(totalPages) { false }
        Book1(
            uriAsString = uri.toString(),
            bookName = name,
            type = type,
            bookLength = totalPages,
            pages = Pages(pages,isPageAvailableArray),
            language = language
        )
    }


    private var book1: Book1? = null

    val bookLength:Int by lazy{
        getText!!.getTotalPages()
    }
    init {


        Log.d("tag","init")

        uiScope.launch {
            book1 = withContext(Dispatchers.IO) {
                    Log.d("book","getting book from database")
                    book2

            }
            //              while (book1==null) delay(50)
//                try {
//                    Log.d("book","initialising current page")
//

                    initialiseCurrentPage()
                    initialiseCardTextList()
//
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
        }
        Log.d("book","updating pages")
        workScope.launch {
            updatePagesAccordingToType()
        }
    }


    //initiating read pdf class if type is pdf
    private val getText: GetText? = null
        get()  {
            Log.d("get","get text is inititiated")
            return field?:GetText(book1?.type?:book2.type, context.contentResolver.openInputStream(uri)!!)
        }


    //current page number
    val currentPageNum: MutableLiveData<Int> by lazy { MutableLiveData(1) }


    //filling in the pages asynchronously if they aren't filled
    private suspend fun updatePagesAccordingToType() {
        while(book1==null) delay(100)
        Log.d("book","wait finished")

        if (book1!!.isListUpdated.not()) {

            val totalPages = book1!!.bookLength
            for (pageNum in   1..totalPages) {
                workScope.launch {
                    yield()
                    if (!isCurrentPageInBook1Pages(pageNum,book1!!)) {
                        val text = getText!!.getTextAtPage(pageNum)
                        addPage(pageNum,book1!!,text)
                    }

                }
            }



        }


        book1?.isListUpdated = true
    }


    //setting page sentences as a mediator data which returns current page text's array according to currentPageNumber
    val currentPageText = MediatorLiveData<String>()

    private  fun initialiseCurrentPage() {
        currentPageText.addSource(currentPageNum) {
            currentPageText.postValue( readCurrentPage(it))
        }

    }

    //method to get Current page's text
    private  fun readCurrentPage(pageNumber:Int) :String {
        // if asked page number is less then or equal to the current size of our array then simply return the text from array
        return  if (isCurrentPageInBook1Pages(pageNumber,book1!!)){
            getTextFromBookAt(pageNumber,book1!!)
        }
        //else return it from the api
        else{
            val text = getText!!.getTextAtPage(pageNumber)
            addPage(pageNumber,book1!!,text)
            text
        }


    }

    //list of text to display on cards
    val cardTextList = MediatorLiveData<List<String>>()
    private  fun initialiseCardTextList() {
        cardTextList.addSource(currentPageNum) {
            cardTextList.postValue( readCurrentPage(it).split("."))
        }

    }

    //function to increase the current page number till the end comes
    fun increaseCurrentPageNumTillEnd() = runBlocking{
        while (book1==null) delay(100)
        Log.d("pages","book length is ${book1!!.bookLength}")
        if (currentPageNum.value!! < book1!!.bookLength) currentPageNum.value = currentPageNum.value!!.plus(1)
    }

    //function to increase the current page number till the start comes
    fun decreasePageNum() {
        if (currentPageNum.value!! > 1) currentPageNum.value = currentPageNum.value!!.minus(1)
    }


    //defining tts
    private val tts = TTS(application.applicationContext)
    fun synthesizeAndSpeak(){
        val myHashRender: HashMap<String, String> = HashMap()
        val textToConvert = "this is a demo for saving a WAV file"
        val destinationFile =  File(context.cacheDir,"test.wav")
        val params = Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, " ")
        tts.tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onDone(utteranceId: String?) {
                Log.d("tts","done $utteranceId")
            }

            override fun onError(utteranceId: String?) {
                Log.d("tts","error")
            }

            override fun onStart(utteranceId: String?) {
                Log.d("tts","start")
            }

        })
        tts.tts.synthesizeToFile(textToConvert,params,destinationFile,"synthesize")

    }
    override  fun onCleared() {
        super.onCleared()
        uiScope.launch {
            workScope.cancel()
        }

    }


}
