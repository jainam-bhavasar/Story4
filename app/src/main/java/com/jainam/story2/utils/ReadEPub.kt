package com.jainam.story2.utils

import nl.siegmann.epublib.domain.Book
import nl.siegmann.epublib.domain.Resource
import nl.siegmann.epublib.domain.Spine
import nl.siegmann.epublib.epub.EpubReader
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class ReadEPub(inputStream: InputStream) {
    private val book: Book = EpubReader().readEpub(inputStream)
    private val spine: Spine = book.spine
    private val spineReferences = spine.spineReferences
    val totalSections = spineReferences.size
    private lateinit var res: Resource


    //getting text from e-pub by api
    fun getPubTextAtSection(i:Int):String {
        var line: String?
        var sectionText: String? = null
        val string = java.lang.StringBuilder()
        res = spine.getResource(i-1)
        try {
            val `is`: InputStream = res.inputStream
            val reader = BufferedReader(InputStreamReader(`is`))
            try {
                while (reader.readLine().also { line = it } != null) {
                    string.append(line+"\n").toString()
                }
                sectionText= string.replace("<.*?>".toRegex(),"")      //removing all html tags and getting pure text
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sectionText!!
    }

}