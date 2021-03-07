package com.app.coderByte.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.Toast
import com.app.coderByte.imageCaching.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileDescriptor
import java.text.SimpleDateFormat
import java.util.*

fun Context.Toast(msg: String) {
    GlobalScope.launch {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@Toast, msg, Toast.LENGTH_SHORT).show()
        }
    }
}

fun Context.loadImage(url: String, imageView: ImageView, placeholder: String = "") {
    val imageLoader = ImageLoader.getInstance(imageView.context)
    if (placeholder.isNotEmpty()) {
        imageLoader.loadBitmap(placeholder, imageView)
    }
    imageLoader.loadBitmap(url, imageView)
}

fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

fun parseDate(date: String?): String {
    var newDateData = date
    date?.let {
        try {
            var spf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS") // server
            val newDate = spf.parse(date)
            spf = SimpleDateFormat("MMM dd, yyyy hh:mm a") // convert formate
            newDateData = spf.format(newDate)
            return newDateData.toString()
        } catch (e: Exception) {
            return ""
        }
    }
    return date ?: ""
}

fun Context.loadJSONFromAssets(fileName: String): String {
    return applicationContext.assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}
fun decodeSampledBitmapFromResource(
    res: Resources?,
    resId: Int,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeResource(res, resId, options)

    // Calculate the sampling rate of inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeResource(res, resId, options)
}

fun decodeSampledBitmapFromFileDescriptor(
    fd: FileDescriptor?,
    reqWidth: Int,
    reqHeight: Int
): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFileDescriptor(fd, null, options)
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inJustDecodeBounds = false
    return BitmapFactory.decodeFileDescriptor(fd, null, options)
}

/**
 * Get the sample rate and zoom ratio of the picture
 *
 * @param options
 * @param reqWidth
 * @param reqHeight
 * @return
 */
fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    var inSampleSize = 1
    if (reqWidth == 0 || reqHeight == 0) return inSampleSize
    val height = options.outHeight
    val width = options.outWidth
    if (height > reqHeight || width > reqWidth) {
        val halHeight = height / 2
        val halWidth = width / 2
        while (halHeight / inSampleSize >= reqHeight &&
            halWidth / inSampleSize >= reqWidth
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}


