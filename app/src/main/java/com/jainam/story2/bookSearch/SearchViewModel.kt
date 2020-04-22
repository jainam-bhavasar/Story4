package com.jainam.story2.bookSearch

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.jainam.story2.database.BookDatabase
import com.jainam.story2.database.Thumbnail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(  application: Application) : AndroidViewModel(application) {

      var allThumbnails:LiveData<List<Thumbnail>> = BookDatabase.getInstance(application).thumbnailDatabaseDao.getAllThumbnails()

    val thumbnailListWithName = MediatorLiveData<List<Thumbnail>>()
     fun initialiseThumbnailListWithName(allThumbnail: List<Thumbnail>){
        thumbnailListWithName.addSource(bookName){ name ->
            var list = arrayListOf<Thumbnail>()
                for (thumbnail in allThumbnail){
                    if (thumbnail.bookName=="fsd.pdf"){
                       list.add(thumbnail)
                    }
                }
            thumbnailListWithName.postValue(list)
        }

    }

    val bookName = MutableLiveData<CharSequence>("")

}
