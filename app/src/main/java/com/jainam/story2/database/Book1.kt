package com.jainam.story2.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jainam.story2.utils.Type


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Entity
data class Book1 (val uriAsString: String,
                  var bookName:String = "",
                  @PrimaryKey (autoGenerate = true)
                  var bookID:Long = 0L,
                  var bookText: String = "",
                  var bookLength:Int = 0,
                  var isPageAvailableArray:String ,
                  var type: Type,
                  var isListUpdated:Boolean  = false
)