package com.jainam.story2.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.jainam.story2.database.MyBook


class Entities(private val mBook: MyBook, val context: Context) {

    private var mCurrentEntityName: Entity = Entity.WORD
    private var mIsLastPage = false
    private var mIsLastSentence = false
    private var mIsLastWord = false
    private val mPage = Page(1, mBook, context)



        fun getNextEntity(swiped: Boolean) {
            when(mCurrentEntityName){
                Entity.PAGE->{if (swiped) {
                    nextPage()
                } else {
                    nextSentence()
                }}
                Entity.SENTENCE->nextSentence()
                Entity.WORD->nextWord()
            }
        }

        fun getPreviousEntity(swiped: Boolean) {
            when(mCurrentEntityName){
                Entity.PAGE->previousPage()
                Entity.SENTENCE->previousSentence()
                Entity.WORD->previousWord()
            }
        }

        fun switchEntityForward() {
            when(mCurrentEntityName){
                Entity.WORD->wordToSentence()
                Entity.SENTENCE->sentenceToPage()
                Entity.PAGE->pageToWord()
            }
        }

        private fun pageToWord() {
            val page = mPage
            page.setWordToCurrentSentenceHead(page.mSentencePositionInPage)
            mCurrentEntityName = Entity.WORD
        }

        private fun wordToSentence() {
            val page = mPage
            page.mSentencePositionInPage = page.getCurrentWordTriple().third
            mCurrentEntityName = Entity.SENTENCE
        }

        private fun sentenceToPage() {
            mPage.mSentencePositionInPage = 0
            mCurrentEntityName = Entity.PAGE
        }

        fun switchEntityBackward() {

            when(mCurrentEntityName){
                Entity.WORD->wordToPage()
                Entity.SENTENCE->sentenceToWord()
                Entity.PAGE->pageToSentence()
            }
        }

        private fun pageToSentence() {
            mCurrentEntityName = Entity.SENTENCE
        }

        private fun sentenceToWord() {
            val page = mPage
            page.setWordToCurrentSentenceHead(page.mSentencePositionInPage)
            mCurrentEntityName = Entity.WORD
        }

        private fun wordToPage() {
            mPage.mSentencePositionInPage = 0
            mCurrentEntityName = Entity.PAGE
        }

        val currentEntity: String
            get() {
               return when(mCurrentEntityName){
                    Entity.WORD->mPage.mCurrentWord
                    Entity.SENTENCE->mPage.mCurrentSentence
                    Entity.PAGE-> mPage.getPageText()!!
                }
            }
        val isLastEntity: Boolean
            get() {
                return when(mCurrentEntityName){
                    Entity.WORD->mIsLastWord
                    Entity.SENTENCE->mIsLastSentence
                    Entity.PAGE-> mIsLastPage
                }
            }

        private fun nextSentence(): String {
            if (mPage.mSentencePositionInPage != mPage.getSentenceListSize() - 1) {
                val page = mPage
                page.mSentencePositionInPage = page.mSentencePositionInPage + 1
            } else if (mPage.mPagePosition == mBook.bookLength) {

                mIsLastSentence = true
            } else {
                nextPage()
                mPage.mSentencePositionInPage = 0
            }
            return mPage.getMCurrentSentence()
        }

        private fun previousSentence(): String {
            if (mPage.mSentencePositionInPage != 0) {
                val page = mPage
                page.mSentencePositionInPage = page.mSentencePositionInPage - 1
            } else if (mPage.mPagePosition == 1) {
                Log.d(TAG, "previousSentence: book starts here")
            } else {
                previousPage()
                val page2 = mPage
                page2.mSentencePositionInPage = page2.getSentenceListSize() - 1
            }

            return mPage.getMCurrentSentence()
        }

        private fun nextWord() {
            if (mPage.mWordPositionInPage != mPage.getWordListSize() - 1) {
                val page = mPage
                page.mWordPositionInPage = page.mWordPositionInPage + 1
            } else if (mPage.mPagePosition == mBook.bookLength) {
                mIsLastWord = true

            } else {
                mIsLastWord = false
                nextPage()
                mPage.mWordPositionInPage = 0
            }
        }

        private fun previousWord() {
            if (mPage.mWordPositionInPage != 0) {
                mIsLastWord = false
                val page = mPage
                page.mWordPositionInPage = page.mWordPositionInPage - 1
            } else if (mPage.mPagePosition != 1) {
                mIsLastWord = false
                previousPage()
                val page2 = mPage
                page2.mWordPositionInPage = page2.getWordListSize() - 1
            }
        }

        private fun previousPage() {
            if (mPage.mPagePosition > 1) {
                val page = mPage
                page.mPagePosition = page.mPagePosition - 1
                val page2 = mPage
                page2.mWordPositionInPage = page2.getWordListSize() - 1
                val page3 = mPage
                page3.mSentencePositionInPage = page3.getSentenceListSize() - 1
                if (mPage.mPagePosition == mBook.bookLength) {
                    mIsLastPage = true
                }
            }
        }

        private fun nextPage() {
            if (mPage.mPagePosition < mBook.bookLength) {
                val page = mPage
                page.mPagePosition = page.mPagePosition + 1
                mPage.mSentencePositionInPage = 0
                mPage.mWordPositionInPage = 0
                return
            }
            mIsLastPage = true
        }



}