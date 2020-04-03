package com.jainam.story2.utils

import com.itextpdf.kernel.pdf.PdfReader
import nl.siegmann.epublib.epub.EpubReader
import java.io.InputStream

 class GetText(val type: Type, private val inputStream: InputStream) {

    //initiate the class which extracts texts from its type
   private lateinit var pdfReader:ReadPdf
   private lateinit var ePubReader: ReadEPub

    //initiate the object with
    init{
        when(type){
            Type.PDF ->pdfReader = ReadPdf(inputStream)
            Type.EPUB -> ePubReader = ReadEPub(inputStream)
        }
    }

    //function for getting text - pageNum starts from 1
    fun getTextAtPage(pageNum:Int):String{
     return  when(type){
            Type.PDF -> pdfReader.getPdfPageText(pageNum)
            Type.EPUB -> ePubReader.getPubTextAtSection(pageNum)
        }
    }

    //function for getting TotalNumberOfPages
    fun getTotalPages():Int{
        return  when(type){
            Type.PDF -> pdfReader.totalPages
            Type.EPUB -> ePubReader.totalSections
        }
    }
}