package com.jainam.story2.Home


import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.jainam.story2.database.Book1
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.database.Pages
import com.jainam.story2.utils.GetText
import com.jainam.story2.utils.Type
import com.jainam.story2.utils.jsonTypeConvertors
//import com.googlecode.tesseract.android.TessBaseAPI
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class HomeViewModel(private var databaseDao: BookDatabaseDao, application:Application) : AndroidViewModel(application),jsonTypeConvertors {

    var thumbnails  = databaseDao.getAllThumbnails()


    //adding co-routines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var application1=application

    //inserting a book : creating a method which calls another method(i.e insertThumbnail) which runs on IO thread and
    //inserts a new book into it
    fun insert(uri: Uri){
        uiScope.launch {
            val fileDetail = FileDetail()

            //getting the name
            val name :String = fileDetail.getFileDetailFromUri(application1.applicationContext,uri)?.fileName!!
            //specifying the type
            val type = enumValueOf<Type>(name.substringAfterLast('.')
                .toUpperCase(Locale.ROOT))

            //getting the total pages
            val totalPages = getTotalPages(uri,type)
            val booleans:Array<Boolean> = Array(totalPages) { false }
            val pageTexts:Array<String> = Array(totalPages){" "}
            val pages = Pages( pageTexts,booleans)
            val book  = Book1(
                uri.toString(),
                bookName =  name,
                type = type,
                bookLength = totalPages,
                pages  = pages
            )

           insertThumbnail(book)
        }
    }
    fun insert(book1: Book1){
        uiScope.launch {

            insertThumbnail(book1)
        }
    }
    private suspend fun insertThumbnail(book: Book1) {
        withContext(Dispatchers.IO){
            databaseDao.insert(book)
        }
    }


    //deleting all thumbnails: creating a method which calls another method(i.e deleteAllThumbnails) which runs on IO thread and
    //delete all thumbnails
    fun deleteAll(){
        uiScope.launch {
            deleteAllThumbnails()
        }
    }
    private suspend fun deleteAllThumbnails(){
        withContext(Dispatchers.IO){
            databaseDao.deleteAll()
        }
    }


    //function for getting the right intent
    fun pickPdfIntent():Intent{
        val mimeTypes = arrayOf(
//            "application/pdf",
//            "application/epub+zip",
//            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
//            "application/msword"
//        "image/bmp",
            "image/png",
            "application/epub+zip",
            "application/pdf"
        )

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)


        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }


        return intent
    }



    //get total number of pags
    fun getTotalPages(uri: Uri,type: Type):Int{
        val getText = GetText(type,application1.contentResolver.openInputStream(uri)!!)
        return getText.getTotalPages()
    }
    //what happens if we cancel the view model
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}





/**
 * File Detail.
 * 1. Model used to hold file details.
 */
class FileDetail {
    // fileSize.
    var fileName: String? = null

    // fileSize in bytes.
    var fileSize: Long = 0

    fun getFileDetailFromUri(context: Context, uri: Uri): FileDetail? {
        val fileDetail: FileDetail?
        fileDetail = FileDetail()
        // File Scheme.
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            val file = File(uri.path!!)
            fileDetail.fileName = file.name
            fileDetail.fileSize = file.length()
        } else if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val returnCursor: Cursor? =
                context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null && returnCursor.moveToFirst()) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
                fileDetail.fileName = returnCursor.getString(nameIndex)
                fileDetail.fileSize = returnCursor.getLong(sizeIndex)
                returnCursor.close()
            }
        }
        return fileDetail
    }

}
