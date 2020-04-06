package com.jainam.story2.utils

import androidx.room.TypeConverter
import com.jainam.story2.database.Pages
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration


class ExtensionTypeConverters {

    @TypeConverter
    fun toType(value: String) = enumValueOf<Type>(value)

    @TypeConverter
    fun fromType(value: Type) = value.name

    @TypeConverter
    fun fromPagesToJson(value: Pages) :String {
        val json = Json(JsonConfiguration.Stable)
        val jsonElement = json.toJson(Pages.serializer(),value)
        return jsonElement.toString()
    }

    @TypeConverter
    fun fromStringToPages(value: String): Pages {
        val json = Json(JsonConfiguration.Stable)
        return json.parse(Pages.serializer(),value)
    }
}