package com.jainam.story2.bookSearch

import android.app.Application
import androidx.lifecycle.*
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.database.BookMetaData

class SearchViewModel(  application: Application) : AndroidViewModel(application) {

      var allThumbnails:LiveData<List<BookMetaData>> = BookDatabase.getInstance(application).thumbnailDatabaseDao.getAllThumbnails()



    val bookName = MutableLiveData<CharSequence>("")

}
