package com.jainam.story2

import android.content.res.AssetManager
import com.jainam.story2.utils.ReadPdf
import com.jainam.story2.utils.ReadPdfOCR
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {

       // val readPdfOCR = ReadPdfOCR(am!!)
        //val string = readPdfOCR.getText(readPdfOCR.getPageBitmap(2))


        assertEquals(4, 2 + 2)
    }
}
