package com.app.coderByte.utils

import android.content.Context
import android.widget.ImageView
import android.widget.Toast
import com.app.coderByte.imageCaching.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

fun Context.Toast(msg: String) {
    GlobalScope.launch {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@Toast, msg, Toast.LENGTH_SHORT).show()
        }
    }
}

fun Context.loadImage(url: String, imageView: ImageView,placeholder: String ="") {
   val  imageLoader = ImageLoader.getInstance(imageView.context)
    if(placeholder.isNotEmpty()){
        imageLoader.loadBitmap(placeholder,imageView)
    }
    imageLoader.loadBitmap(url,imageView)
//    Glide.with(imageView.context)
//            .load(url)
//            .apply(
//                RequestOptions()
//            )
//            .into(imageView)
}
fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

fun Context.parseDate(date: String?): String {
    var newDateData = date
    try {
        var spf = SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS") // server
        val newDate = spf.parse(date)
        spf = SimpleDateFormat("MMM dd, yyyy hh:mm a") // convert formate
        newDateData = spf.format(newDate)

        return newDateData
    } catch (e: Exception) {
    }
    return date?:""
}
fun Context.loadJSONFromAssets(fileName: String): String {
    return applicationContext.assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}

