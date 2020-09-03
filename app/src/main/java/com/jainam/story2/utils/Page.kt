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
                mSentenceList = languageFullStopCharacter.let { pageText!!.split(it) }
                mWordList = createWordListFromSentenceList(mSentenceList)
            }
        }




    private lateinit var mSentenceList: List<String>
    var mSentencePositionInPage = 0
    private var mWordList: ArrayList<Triple<String, Int, Int>>? = null
    var mWordPositionInPage = 0


     var pageText: String? = getText?.getTextAtPage(pageNum)




    fun getCurrentWordTriple(): Triple<String, Int, Int> {
        return mWordList!![mWordPositionInPage]
    }

    fun setWordToCurrentSentenceHead(sentenceNum: Int) {
        for (wordTriple in mWordList!!){
            if (wordTriple.third == sentenceNum){
                mWordPositionInPage = wordTriple.second
                break
            }
        }
    }

    val mCurrentWord: String
        get() {
        return mWordList!![mWordPositionInPage].first
    }

    val mCurrentSentence:String get()= mSentenceList[mSentencePositionInPage]

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
