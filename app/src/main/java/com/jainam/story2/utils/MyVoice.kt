package com.jainam.story2.utils

import androidx.room.Entity
import androidx.room.PrimaryKey

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
@Entity
class MyVoice ( @PrimaryKey val language:String,
                var countryCode:String,
                var voiceName:String)