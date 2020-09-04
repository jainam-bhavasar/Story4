package com.jainam.story2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jainam.story2.utils.MyVoice

@Database(entities = [BookMetaData::class,MyVoice::class],version = 1,exportSchema = false)
abstract class BookDatabase :RoomDatabase(){
    abstract val thumbnailDatabaseDao:BookDatabaseDao

    companion object{

        @Volatile
        private var INSTANCE:BookDatabase? = null

        fun getInstance(context: Context):BookDatabase{
            synchronized(this){
                var instance = INSTANCE

                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BookDatabase::class.java,
                        "database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}