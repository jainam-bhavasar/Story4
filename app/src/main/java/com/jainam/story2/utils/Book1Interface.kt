package com.jainam.story2.utils

import com.jainam.story2.database.Book1

interface Book1Interface {
    //add pageText to book at given page number and also set the value of is filled with
    fun addPage(pageNum:Int,book1:Book1,text:String){
        book1.pages.pagTexts[pageNum-1] =text
        book1.pages.isPageAvailableArray[pageNum-1] = true
    }
     fun isCurrentPageInBook1Pages(pageNum: Int,book1: Book1):Boolean{
        return book1.pages.isPageAvailableArray[pageNum-1]
    }

    fun getTextFromBookAt(pageNum: Int,book1: Book1):String{
       return book1.pages.pagTexts[pageNum.minus(1)]
    }


}