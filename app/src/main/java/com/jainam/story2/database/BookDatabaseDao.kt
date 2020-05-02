package com.jainam.story2.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface BookDatabaseDao
{
    @Insert
    fun insert(book1: Book1)

    @Delete
    fun delete(book1: Book1)

    @Update
    fun update(book1: Book1)

    @Query("SELECT * FROM Book1 WHERE uriAsString = :uriString")
    suspend fun get(uriString:String):Book1


    @Query("SELECT bookID,bookName,uriAsString FROM Book1 ORDER BY bookID DESC")
    fun getAllThumbnails():LiveData<List<Thumbnail>>

    @Query("SELECT bookID,bookName,uriAsString FROM Book1 ORDER BY bookID DESC")
    fun getAllThumbnailsWithoutLiveData():List<Thumbnail>

    @Query("DELETE FROM Book1")
    fun deleteAll()





//    @Query("SELECT type FROM Book1 WHERE uriAsString =:uriString")
//    fun getType(uriString: String):String

}