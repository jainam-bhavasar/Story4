package com.jainam.story2.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jainam.story2.database.BookDatabaseDao
import java.lang.IllegalArgumentException

class PlayerViewModel2Factory(private val uri: Uri, private val application: Application,
                              private val lastPage :Int,private val lastPosition:Int,
                              private val bookDatabaseDao: BookDatabaseDao,private val language:String,val bookLength:Int ):
    ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
            return PlayerViewModel(
                application = application,
                uri = uri,
                bookDatabaseDao = bookDatabaseDao,
                lastPosition = lastPosition,
                lastPage = lastPage,language = language,bookLength = bookLength) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}