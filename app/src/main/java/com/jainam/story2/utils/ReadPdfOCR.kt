package com.jainam.story2.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.shockwave.pdfium.PdfiumCore
import java.io.*

class ReadPdfOCR(inputStream: InputStream,val context: Context,val uri: Uri) {

    private val parcelFileDescriptor: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
    private val renderer :PdfRenderer=PdfRenderer(parcelFileDescriptor)
    val totalPages :Int = renderer.pageCount
    private  var pageNum = 1



    private fun  bitmap():Bitmap
      {
          val pdfiumCore = PdfiumCore(context.applicationContext)
          val pd: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
          val pdfDocument = pdfiumCore.newDocument(pd)
          val width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum)
          val height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum)
          val mBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
          pdfiumCore.renderPageBitmap(pdfDocument, mBitmap, pageNum, 0, 0, width, height)
          pdfiumCore.closeDocument(pdfDocument) // important!
          return mBitmap
      }

    fun getText(pageNumber: Int):String{
        pageNum = pageNumber
        val image = FirebaseVisionImage.fromBitmap(bitmap())
        val detector = FirebaseVision.getInstance().onDeviceTextRecognizer
        var text = ""
       val result =  detector.processImage(image)
            .addOnSuccessListener { firebaseVisionDocumentText ->
               text = firebaseVisionDocumentText.text
                Log.d("pages",text)
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


}