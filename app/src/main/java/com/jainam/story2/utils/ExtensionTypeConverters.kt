package com.jainam.story2.utils

import androidx.room.TypeConverter


class ExtensionTypeConverters {

    @TypeConverter
    fun toType(value: String) = enumValueOf<Type>(value)

    @TypeConverter
    fun fromType(value: Type) = value.name

}