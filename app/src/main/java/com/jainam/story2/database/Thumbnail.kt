package com.jainam.story2.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Entity
data class Thumbnail (
    @PrimaryKey(autoGenerate = true)
    val thumbnailID:Long = 0L,
    val thumbnailName:String = "hello",
    val uriAsString:String,
    val type: String,
    var lastPage:Int = 1,
    var lastPosition :Int = 0,
    var language:String = "en",
    var bookLength:Int = 1
)