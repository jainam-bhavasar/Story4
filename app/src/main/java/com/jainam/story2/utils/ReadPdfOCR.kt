package com.jainam.story2.utils

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.os.ParcelFileDescriptor.MODE_READ_ONLY
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.*
import java.net.URI

class ReadPdfOCR(inputStream: InputStream) {

    private val parcelFileDescriptor: ParcelFileDescriptor
    private val renderer :PdfRenderer
    val totalPages :Int
    private  var pageNum = 1
    init {
        parcelFileDescriptor=ParcelFileDescriptor.open(createFileFromInputStream(inputStream),MODE_READ_ONLY)
        renderer= PdfRenderer(parcelFileDescriptor)
        totalPages = renderer.pageCount
    }
   private val  bitmap:Bitmap
       get() {
       val page = renderer.openPage(pageNum-1)
       val ratio = page.width.toFloat()/page.height
       val mBitmap:Bitmap = Bitmap.createBitmap(360,(ratio*360).toInt(),Bitmap.Config.ARGB_8888)
       page.render(mBitmap,null,null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
       return mBitmap
   }

    fun getText(pageNumber: Int):String{
        pageNum = pageNumber
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().cloudDocumentTextRecognizer
        var text = ""
        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionDocumentText ->
               text = firebaseVisionDocumentText.text
                // Task completed successfully
                // ...
            }
            .addOnFailureListener { e ->
                text = ""
                // Task failed with an exception
                // ...
            }
        return text
    }

    private fun createFileFromInputStream(inputStream: InputStream): File? {
        try {
            val f = File("new FilePath")
            val outputStream: OutputStream = FileOutputStream(f)
            val buffer = ByteArray(1024)
            var length = 0
            while (inputStream.read(buffer).also({ length = it }) > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.close()
            inputStream.close()
            return f
        } catch (e: IOException) {
            //Logging exception
        }
        return null
    }

}