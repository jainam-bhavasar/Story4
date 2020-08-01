package com.jainam.story2.home


//import com.google code. tesseract .android.TessBaseAPI
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import com.jainam.story2.database.BookDatabaseDao
import com.jainam.story2.database.Thumbnail
import com.jainam.story2.utils.GetText
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream


class HomeViewModel(private var databaseDao: BookDatabaseDao, application:Application) : AndroidViewModel(application) {

    var thumbnails  = databaseDao.getAllThumbnails()
    private val context = application.applicationContext
    private val application1 = application
    //adding co-routines
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var workScope = CoroutineScope(Dispatchers.Default + viewModelJob)
    //inserting a book : creating a method which calls another method(i.e insertThumbnail) which runs on IO thread and
    //inserts a new book into it
    fun insert(uri: Uri){
        workScope.launch {
            val fileDetail = FileDetail()

            //getting the name
            val name :String = fileDetail.getFileDetailFromUri(context,uri)?.fileName!!


            val nameWithoutSuffix = name.removeSuffix(".pdf")

            val type:com.jainam.story2.utils.Type = getTypeFromName(name)

           //get uri of copied file
            val uriOfCopiedFile = uriToFileInInternalStorage(uri)

            val getText: GetText? = context.contentResolver.openInputStream(uriOfCopiedFile)?.let { GetText(type = type,inputStream = it) }


            val thumbnail  = Thumbnail(
                thumbnailName = nameWithoutSuffix,
                uriAsString = uriOfCopiedFile.toString(),
                type = type.name,
                language = getText?.getLanguage()?:"en",
                bookLength = getText?.getTotalPages()?:1
            )

           insertThumbnail(thumbnail)
        }
    }



    private suspend fun insertThumbnail(thumbnail: Thumbnail) {
        withContext(Dispatchers.IO){
            databaseDao.insert(thumbnail)
        }
    }


    //deleting all thumbnails: creating a method which calls another method(i.e deleteAllThumbnails) which runs on IO thread and
    //delete all thumbnails



    fun  delete(thumbnail: Thumbnail){
        uiScope.launch {
            withContext(Dispatchers.IO){
                databaseDao.delete(thumbnail)
            }
        }

    }


    //function for getting the right intent
    fun pickPdfIntent():Intent{
        val mimeTypes = arrayOf(
            "application/epub+zip",
            "application/pdf"
        )

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION

        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.isNotEmpty()) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }


        return intent
    }



    //what happens if we cancel the view model
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

     fun uriToFileInInternalStorage(uri: Uri):Uri{
         val inputStream = application1.contentResolver.openInputStream(uri)
         val name :String = FileDetail().getFileDetailFromUri(context,uri)?.fileName!!
         val outputFile = File("${application1.filesDir.absoluteFile}/$name")
         val output = FileOutputStream(outputFile)
         inputStream?.copyTo(output, 4 * 1024)
         return Uri.fromFile(outputFile)
    }


}


fun getTypeFromName(name:String):com.jainam.story2.utils.Type{
    val extension = name.substringAfterLast(".")
    return if (extension == "pdf")com.jainam.story2.utils.Type.PDF else com.jainam.story2.utils.Type.EPUB
}


/**
 * File Detail.
 * 1. Model used to hold file details.
 */
class FileDetail {
    // fileSize.
    var fileName: String? = null

    fun getFileDetailFromUri(context: Context, uri: Uri): FileDetail? {
        val fileDetail: FileDetail?
        fileDetail = FileDetail()
        // File Scheme.
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            val file = File(uri.path!!)
            fileDetail.fileName = file.name

        } else if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val returnCursor: Cursor? =
                context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null && returnCursor.moveToFirst()) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileDetail.fileName = returnCursor.getString(nameIndex)

                returnCursor.close()
            }
        }
        return fileDetail
    }



}
