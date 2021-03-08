package com.app.coderByte.imageCaching

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.app.coderByte.R
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock

class ImageCompressTest {

    @Mock
    private val bitmap: Bitmap? = null


    @Test
    fun testResouceId() {
        assertEquals(
            R.drawable.dubizzle_logo, com.app.coderByte.utils.getResId(
                "dubizzle_logo",
                R.drawable::class.java
            )
        )
    }

    @Test
    fun calculateInSampleSize() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.outHeight = 20
        options.outWidth = 20
        assertEquals(1, com.app.coderByte.utils.calculateInSampleSize(options,0,0))

    }
    @Test
    fun calculateInSampleSize20() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.outHeight = 100
        options.outWidth = 100
        assertEquals(2, com.app.coderByte.utils.calculateInSampleSize(options,40,40))

    }
}