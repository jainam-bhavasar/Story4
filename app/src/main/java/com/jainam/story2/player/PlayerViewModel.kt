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
import com.jainam.story2.database.Pages
import com.jainam.story2.utils.*
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.util.*



class PlayerViewModel(  application:Application, private val uri: Uri, private var databaseDao: BookDatabaseDao) : AndroidViewModel(application),jsonTypeConvertors {

    //getting book from database
    private val context = application.applicationContext

    //defining scopes
    private var viewModelJob = Job()
    private val workScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    //If database query fails then initialise a book with the same uri and use this
    private val book2: Book1
        get() {
            val name = FileDetail().getFileDetailFromUri(context, uri)!!.fileName
            val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))
            val totalPages =
                GetText(type, context.contentResolver.openInputStream(uri)!!).getTotalPages()
            val pages = Array(totalPages){" "}
            val isPageAvailableArray = Array(totalPages) { false }
            return Book1(
                uriAsString = uri.toString(),
                bookName = name,
                type = type,
                bookLength = totalPages,
                pages = Pages(pages,isPageAvailableArray)
            )
        }


    //page availability list
    private lateinit var isPageAvailableArray: Array<Boolean>

    private var book1: Book1? = null
    private var mainJob:Job?  = null

    init {

       mainJob =  workScope.launch {
            try {

                book1 = withContext(Dispatchers.IO) {
                    try {
                        databaseDao.get(uri.toString())
                    }catch (e:IOException){
                        book2
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            uiScope.launch {
                while (book1==null) delay(100)
                try {
                    initialiseCurrentPage()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            updatePagesAccordingToType()
        }
    }

    //initiating read pdf class if type is pdf
    private val getText: GetText? = null
        get() {
            return field ?: GetText(book1!!.type, context.contentResolver.openInputStream(uri)!!)
        }



    //current page number
    private val currentPageNum: MutableLiveData<Int> by lazy { MutableLiveData(1) }


    //filling in the pages asynchronously if they aren't filled
    private suspend fun updatePagesAccordingToType() {
        while(book1==null) delay(100)
        if (book1!!.isListUpdated.not()) {

            val totalPages = book1!!.bookLength
                for (pageNum in   1..totalPages) {
                 workScope.launch {
                        if (!isCurrentPageInBook1Pages(pageNum)) {
                            addPage(pageNum)
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
        return  if (isCurrentPageInBook1Pages(pageNumber)){
            book1!!.pages.pagTexts[pageNumber.minus(1)]
        }
        //else return it from the api
        else{
            val text = getText!!.getTextAtPage(pageNumber)
            addPage(pageNumber)
            text
        }


    }

    private fun addPage(pageNum:Int){
        book1!!.pages.pagTexts[pageNum-1] = getText!!.getTextAtPage(pageNum)
        book1!!.pages.isPageAvailableArray[pageNum-1] = true
    }

    private fun isCurrentPageInBook1Pages(pageNum: Int):Boolean{
        return book1!!.pages.isPageAvailableArray[pageNum-1]
    }
    //function to increase the current page number till the end comes
     fun increaseCurrentPageNumTillEnd() = runBlocking{
        while (book1==null) delay(100)
        Log.d("pages","book length is ${book1!!.bookLength}")
        if (currentPageNum.value!! < book1!!.bookLength) currentPageNum.value = currentPageNum.value!!.plus(1)
    }


    override  fun onCleared() {
        super.onCleared()
        uiScope.launch {
            workScope.cancel()
        }
        mainJob!!.cancelChildren()
    }
}
