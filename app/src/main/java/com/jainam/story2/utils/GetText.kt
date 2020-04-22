package com.jainam.story2.utils

import android.util.Log
import java.io.InputStream

class GetText(val type: Type, private val inputStream: InputStream):GetLang {

    //initiate the class which extracts texts from its type
    private lateinit var pdfReader:ReadPdf
    private lateinit var ePubReader: ReadEPub

    //initiate the object with
    init{
        Log.d("pages","getText Init happened")
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


    // get book language
    fun getLanguage():String{
        //getting sample text
        var sampleText = ""
        var pageNum = 1
        while (sampleText.length <1000){
            sampleText+=getTextAtPage(pageNum)
            pageNum++
        }
        return getLangFromText(sampleText)
    }
}