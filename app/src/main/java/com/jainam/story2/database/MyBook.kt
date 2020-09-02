package com.jainam.story2.database

import com.jainam.story2.utils.Type



data class MyBook (val uriAsString: String,
                   var bookLength:Int = 0,
                   var type: Type,
                   val language :String
)