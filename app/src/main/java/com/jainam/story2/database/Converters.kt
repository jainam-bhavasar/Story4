package com.jainam.story2.database

import androidx.room.TypeConverter
import com.google.gson.Gson


class Converters {


    @TypeConverter
    fun listToJson(value: Array<Array<String>>):String = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Array<String>>::class.java)
}
