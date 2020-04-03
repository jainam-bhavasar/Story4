package com.jainam.story2.player

import android.app.Application
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jainam.story2.database.BookDatabaseDao
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class PlayerViewModelFactory(private val dataSource: BookDatabaseDao, private val application: Application, private val uri: Uri): ViewModelProvider.Factory  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PlayerViewModel::class.java)){
            return PlayerViewModel(application,uri,dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}