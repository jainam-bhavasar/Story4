package com.jainam.story2.utils

import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor
import java.io.InputStream

class ReadPdf(inputStream: InputStream) {


    //creating pdf reader from the input stream
    private val reader = PdfReader(inputStream)

    //creating document from reader
    private val document = PdfDocument(reader)

    //total pages
    val totalPages = document.numberOfPages

    //suspend function to extract pdf text of current page
    fun getPdfPageText(pageNum: Int):String{
        val page = document.getPage(pageNum)
        return PdfTextExtractor.getTextFromPage(page)

    }


}