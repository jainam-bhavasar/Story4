package com.jainam.story2.utils

import android.util.Log
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

const val TAG: String = "lang"

interface GetLang {





    fun getLangFromText(text:String):String  = runBlocking{

        val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
        val languageIdentificationTask = languageIdentifier.identifyLanguage(text)
        while (!languageIdentificationTask.isComplete) delay(50)
        return@runBlocking languageIdentificationTask.result!!


    }

}