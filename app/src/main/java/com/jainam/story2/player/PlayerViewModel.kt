package com.jainam.story2.player

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jainam.story2.Home.FileDetail
import com.jainam.story2.database.Book1
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.utils.*
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class PlayerViewModel(  application:Application, private val uri: Uri, private var databaseDao: BookDatabaseDao) : AndroidViewModel(application),jsonTypeConvertors  {

    //getting book from database
    private val context = application.applicationContext

    //defining scopes
    private var viewModelJob = Job()
    private val workScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //If database query fails then initialise a book with the same uri and use this
    private val book2:Book1
    get() {
        val name = FileDetail().getFileDetailFromUri(context,uri)!!.fileName
        val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))
        val totalPages = GetText(type,context.contentResolver.openInputStream(uri)!!).getTotalPages()
        val isPageAvailableArray = Array(totalPages){false}
        return Book1(uriAsString = uri.toString(),bookName = name,type = type,bookLength = totalPages,isPageAvailableArray = booleanListToJson(isPageAvailableArray))
    }


    //page availability list
    private  lateinit var isPageAvailableArray: Array<Boolean>

    private lateinit var book1:Book1
    init {

        workScope.launch {
            try {

                book1 = try {
                    withContext(Dispatchers.IO){
                        databaseDao.get(uri.toString())
                    }
                }catch(e:Exception){
                    book2
                }
                pages.postValue(convertingBookTextToArrayJob())
                isPageAvailableArray = fromStringToBooleanList(book1.isPageAvailableArray)
            }catch (e:IOException){
               e.printStackTrace()
            }


            uiScope.launch {
                while (pages.value?.size==0) delay(100)
                try {
                    initialiseCurrentPage()

                }catch (e:Exception){e.printStackTrace()}
            }
            try {
              updatePagesAccordingToType()
            }catch (e:Exception){
                Log.d("pageViewModel",e.toString())

            }
        }
    }


    //initiating read pdf class if type is pdf
    private val getText: GetText? = null
        get() {
            return field ?: GetText(book1.type,context.contentResolver.openInputStream(uri)!!)
        }



    //setting the pages as arrayList of arrayList - initiating it so it is not null
    private  val pages : MutableLiveData<ArrayList<String>> by lazy { MutableLiveData<ArrayList<String>>(arrayListOf()) }


    private  suspend fun  convertingBookTextToArrayJob() :ArrayList<String> {

        return if (book1.bookText !=""){
            fromStringToStringList(book1.bookText)
        }else{
            arrayListOf()
        }
    }




    //current page number
    private val currentPageNum:MutableLiveData<Int> by lazy { MutableLiveData(1) }


    //filling in the pages asynchronously if they aren't filled
    private suspend fun updatePagesAccordingToType() {

        if (book1.isListUpdated.not()) {

            val numOfPagesPresent = pages.value!!.size
            val totalPages = book1.bookLength
            for (i in numOfPagesPresent+1 .. totalPages) {
                if (!isPageAvailableArray[i-1]) pages.value!!.add(getText!!.getTextAtPage(i))
                isPageAvailableArray[i-1] =  true
                Log.i("pages","is page avail set true number ${isPageAvailableArray[i-1]}")
            }

            book1.isListUpdated = true
        }

    }


    //setting page sentences as a mediator data which returns current page text's array according to currentPageNumber
    val currentPageText = MediatorLiveData<String>()
    private suspend fun initialiseCurrentPage() {
        currentPageText.addSource(currentPageNum) {

            workScope.launch {
                currentPageText.postValue( readCurrentPage(it))
            }


        }

    }

    //method to get Current page's text
    private  fun readCurrentPage(pageNumber:Int) :String {
        Log.i("pages","isCurrentPagesAvailable -> ${isPageAvailableArray[pageNumber-1]} ")
        // if asked page number is less then or equal to the current size of our array then simply return the text from array
        return  if (isPageAvailableArray[pageNumber-1]){
            pages.value!![pageNumber.minus(1)]
        }
        //else return it from the api
        else{
            isPageAvailableArray[pageNumber-1] = true
            val text = getText!!.getTextAtPage(pageNumber)
            pages.value!!.add(text)
            text
        }


    }




    //function to increase the current page number till the end comes
    fun increaseCurrentPageNumTillEnd(){
        if (currentPageNum.value!! < pages.value!!.size ) currentPageNum.value = currentPageNum.value!!.plus(1)
    }






}
