package com.jainam.story2.utils

import android.content.Context
import android.util.Log
import com.jainam.story2.database.MyBook
import java.util.*
import kotlin.jvm.internal.Intrinsics


class Page(r12: Int, val myBook: MyBook, r14: Context?) {
    private val getText: GetText? = null
    private val languageFullStopCharacter: String? = null
    private var mCurrentCharacterPosition = 0
    var mPagePosition = 0
        set(value) {
            if (value <= myBook.bookLength && value > 0) {
                field = value
                val textAtPage = getText!!.getTextAtPage(value)
                mSentenceList = languageFullStopCharacter?.let { textAtPage.split(it) }!!
                mWordList = createWordListFromSentenceList(mSentenceList)
            }
        }
    var mSentenceLengthList: List<Int>? = null
        set(list) {
            Intrinsics.checkNotNullParameter(list, "<set-?>")
            field = list
        }
    private lateinit var mSentenceList: List<String>
    var mSentencePositionInPage = 0
    private var mWordList: ArrayList<Triple<String, Int, Int>>? = null
     var mWordPositionInPage = 0
    private var pageText: String? = null
    fun updateCharacterBy(charactersMovedForwards: Int) {
        setMCurrentCharacterPosition(mCurrentCharacterPosition + charactersMovedForwards)
        if (mCurrentCharacterPosition > pageText!!.length - 1) {
            setMCurrentCharacterPosition(pageText!!.length - 1)
        }
    }

    fun getMCurrentCharacterPosition(): Int {
        return mCurrentCharacterPosition
    }

    fun setMCurrentCharacterPosition(value: Int) {
        mCurrentCharacterPosition = value
    }

    fun getPageText(): String? {
        return pageText
    }

    fun setPageText(str: String?) {
        Intrinsics.checkNotNullParameter(str, "<set-?>")
        pageText = str
    }

    fun getSentenceListSize(): Int {
        return mSentenceList!!.size
    }

    fun getMCurrentSentence(): String {
        return mSentenceList!![mSentencePositionInPage]
    }

    fun getWordListSize(): Int {
        return mWordList!!.size
    }

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

    fun getCurrentWord(): String {
        return mWordList!![mWordPositionInPage].first
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
