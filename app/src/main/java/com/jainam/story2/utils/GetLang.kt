package com.jainam.story2.utils

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking


interface GetLang {





    fun getLangFromText(text:String):String  = runBlocking{

        val languageIdentifier = FirebaseNaturalLanguage.getInstance().languageIdentification
        val languageIdentificationTask = languageIdentifier.identifyLanguage(text)
        while (!languageIdentificationTask.isComplete) delay(50)
        return@runBlocking languageIdentificationTask.result!!


    }

}