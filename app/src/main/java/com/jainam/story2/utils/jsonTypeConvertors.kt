package com.jainam.story2.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface jsonTypeConvertors {
    //type converters
     fun stringListToJson(value: ArrayList<String>):String = Gson().toJson(value)
     fun fromStringToStringList(value: String): ArrayList<String> {
         val type = object: TypeToken<ArrayList<String>>() {}.type
         return Gson().fromJson(value, type)

     }

    fun booleanListToJson(value: Array<Boolean>):String = Gson().toJson(value)
    fun fromStringToBooleanList(value: String): Array<Boolean> {
        val type = object: TypeToken<Array<Boolean>>() {}.type
        return Gson().fromJson(value, type)
    }
}