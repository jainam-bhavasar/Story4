package com.jainam.story2.player

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.jainam.story2.database.Book1
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.home.FileDetail
import com.jainam.story2.utils.*
import kotlinx.coroutines.*
import java.util.*


class PlayerViewModel( val context: Context, private val uri: Uri) {



    //defining scopes
    private val uiScope = CoroutineScope(Dispatchers.Main )
    private val workScope  = CoroutineScope(Dispatchers.Default )


    //If database query fails then initialise a book with the same uri and use this
     val book2: Book1 by lazy {
        val name = FileDetail().getFileDetailFromUri(context, uri)!!.fileName
        val type = enumValueOf<Type>(name!!.substringAfterLast('.').toUpperCase(Locale.ROOT))
        val getText1 = GetText(type, context.contentResolver.openInputStream(uri)!!)
        val totalPages = getText1.getTotalPages()
        val language  = getText1.getLanguage()
        Book1(
            uriAsString = uri.toString(),
            bookName = name,
            type = type,
            bookLength = totalPages,

            language = language
        )
    }


    private var book1: Book1? = null

    val bookLength:Int by lazy{
        getText!!.getTotalPages()
    }



    //initiating read pdf class if type is pdf
    private val getText: GetText= GetText(book1?.type?:book2.type, context.contentResolver.openInputStream(uri)!!)



    //current page number
    val currentPageNum: MutableLiveData<Int> by lazy { MutableLiveData(1) }

    fun getCurrentPage(pageNum:Int):String{
        return getText.getTextAtPage(pageNum)
    }


    //Current navigation type
     val currentNavigateBy = MutableLiveData(NavigateBy.SENTENCE)



    //function to increase the current page number till the end comes
    fun increaseCurrentPageNumTillEnd() {
        if (currentPageNum.value!! < book2.bookLength ) currentPageNum.value = currentPageNum.value!!.plus(1)
    }

    //function to increase the current page number till the start comes
    fun decreasePageNum() {
        if (currentPageNum.value!! > 1) currentPageNum.value = currentPageNum.value!!.minus(1)
    }







}
