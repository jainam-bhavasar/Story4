package com.jainam.story2.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.jainam.story2.database.MyBook
import java.net.URI
import java.util.*
import kotlin.jvm.internal.Intrinsics


class Page(pageNum: Int, private val myBook: MyBook, context: Context) {
    private val getText: GetText = GetText(myBook.type, context.contentResolver.openInputStream(Uri.parse(myBook.uriAsString))!!)

    private val languageFullStopCharacter: Char = when(myBook.language){
        "en" -> '.'
        "hi" -> '।'   //hindi
        "bn" -> '।'   //bengali
        "ur" ->'¯'   //urdu
        else -> '.'
    }

    var mPagePosition = pageNum
        set(value) {
            if (value <= myBook.bookLength && value > 0) {
                field = value
                pageText = getText.getTextAtPage(value)
                mSentenceList = languageFullStopCharacter.let { pageText.split(it) }
                mWordList = createWordListFromSentenceList(mSentenceList)
            }
        }



    var pageText: String = getText.getTextAtPage(mPagePosition)

    //sentence stuff
    private  var mSentenceList: List<String> = pageText.split(languageFullStopCharacter)
    var mSentencePositionInPage = 0
    val mCurrentSentence:String get()= mSentenceList[mSentencePositionInPage]
    val sentenceListSize:Int get() = mSentenceList.size

    //word Sruff
    private var mWordList: ArrayList<Triple<String, Int, Int>> = createWordListFromSentenceList(mSentenceList)
     val mWordListSize:Int get() = mWordList.size
    var mWordPositionInPage = 0

    val mCurrentWord: String
        get() {
            return mWordList!![mWordPositionInPage].first
        }

    val mCurrentWordTriple: Triple<String, Int, Int>
        get(){
            return mWordList[mWordPositionInPage]
        }





    fun setWordToCurrentSentenceHead(sentenceNum: Int) {
        for (wordTriple in mWordList!!){
            if (wordTriple.third == sentenceNum){
                mWordPositionInPage = wordTriple.second
                break
            }
        }
    }




   companion object {

        fun createWordListFromSentenceList(mSentenceList: List<String>): ArrayList<Triple<String, Int, Int>> {
            val mWordList: ArrayList<Triple<String, Int, Int>> = arrayListOf()
            for ((sentenceIndex,sentence) in mSentenceList.withIndex()){
                val wordList = sentence.split(' ')
                for ((index,word) in wordList.withIndex()){
                    mWordList.add(Triple(word,index,sentenceIndex))
                }
            }
            return mWordList
        }

    }




}
