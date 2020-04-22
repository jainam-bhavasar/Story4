package com.jainam.story2

import android.content.Context
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.jainam.story2.utils.GetLang
import com.jainam.story2.utils.GetText
import com.jainam.story2.utils.Type
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.*
import java.lang.Exception

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest :GetLang{
    @Test
    fun testGetLangOfBook(){
        fun get2Things(int: Int,string: String):Pair<Int,String>{
            return Pair(int,string)
        }
        println(get2Things(1,"jainam").toString())
        assertEquals(2,1+1)

    }
}
