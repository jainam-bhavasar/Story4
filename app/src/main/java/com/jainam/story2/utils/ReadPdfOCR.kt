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
import java.io.*

class ReadPdfOCR(inputStream: InputStream,val context: Context,val uri: Uri) {

    private val parcelFileDescriptor: ParcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")!!
    private val renderer :PdfRenderer=PdfRenderer(parcelFileDescriptor)
    val totalPages :Int = renderer.pageCount
    private  var pageNum = 1





    fun getText(pageNumber: Int):String{
//

        return " "
    }


}