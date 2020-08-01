package com.jainam.story2.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.jainam.story2.utils.MyVoice

@Dao
interface BookDatabaseDao
{


    @Insert
    fun insert(thumbnail: Thumbnail)

    @Delete
    fun delete(thumbnail: Thumbnail)

    @Update
    fun update(thumbnail: Thumbnail)

    @Query("SELECT * FROM Thumbnail WHERE uriAsString = :uriString")
    suspend fun get(uriString:String):Thumbnail


    @Query("SELECT * FROM Thumbnail ORDER BY thumbnailID DESC")
    fun getAllThumbnails():LiveData<List<Thumbnail>>

    @Query("SELECT * FROM MyVoice WHERE language = :language")
    fun getVoiceOfLang(language:String):MyVoice?

    @Update
    fun updateMyVoice(myVoice: MyVoice)

    @Insert
    fun insertMyVoice(myVoice: MyVoice)


}