package com.jainam.story2.database

import kotlinx.serialization.Serializable

@Serializable
data class Pages(val pagTexts:Array<String>,
                 val isPageAvailableArray:Array<Boolean>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Pages

        if (!pagTexts.contentEquals(other.pagTexts)) return false
        if (!isPageAvailableArray.contentEquals(other.isPageAvailableArray)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pagTexts.contentHashCode()
        result = 31 * result + isPageAvailableArray.contentHashCode()
        return result
    }
}