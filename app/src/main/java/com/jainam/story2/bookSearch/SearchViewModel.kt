package com.jainam.story2.bookSearch

import android.app.Application
import androidx.lifecycle.*
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.database.Thumbnail

class SearchViewModel(  application: Application) : AndroidViewModel(application) {

      var allThumbnails:LiveData<List<Thumbnail>> = BookDatabase.getInstance(application).thumbnailDatabaseDao.getAllThumbnails()



    val bookName = MutableLiveData<CharSequence>("")

}
