package com.jainam.story2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jainam.story2.utils.MyVoice

@Dao
interface BookDatabaseDao
{


    @Insert
    fun insert(bookMetaData: BookMetaData)

    @Delete
    fun delete(bookMetaData: BookMetaData)

    @Update
    fun update(bookMetaData: BookMetaData)

    @Query("SELECT * FROM BookMetaData WHERE uriAsString = :uriString")
    suspend fun get(uriString:String):BookMetaData


    @Query("SELECT * FROM BookMetaData ORDER BY thumbnailID DESC")
    fun getAllThumbnails():LiveData<List<BookMetaData>>

    @Query("SELECT * FROM MyVoice WHERE language = :language")
    fun getVoiceOfLang(language:String):MyVoice?

    @Update
    fun updateMyVoice(myVoice: MyVoice)

    @Insert
    fun insertMyVoice(myVoice: MyVoice)


}