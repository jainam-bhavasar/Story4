package com.jainam.story2.Home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jainam.story2.database.BookDatabaseDao
import java.lang.IllegalArgumentException

class HomeViewModelFactory(private val dataSource:BookDatabaseDao, private val application: Application):ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
            return HomeViewModel(dataSource,application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}